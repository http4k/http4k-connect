package org.http4k.connect.amazon.dynamodb.mapper

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.onFailure
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.dynamodb.*
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.model.*
import org.http4k.format.AutoMarshalling
import org.http4k.format.autoDynamoLens
import org.http4k.lens.BiDiLens

private const val BATCH_PUT_LIMIT = 25  // as defined by DynamoDB
private const val BATCH_GET_LIMIT = 100 // as defined by DynamoDB

class DynamoDbTableMapper<Document : Any, HashKey : Any, SortKey : Any>(
    private val dynamoDb: DynamoDb,
    private val tableName: TableName,
    private val itemLens: BiDiLens<Item, Document>,
    private val primarySchema: DynamoDbTableMapperSchema<HashKey, SortKey>
) {

    private fun Document.key(): Key {
        val item = Item().with(itemLens of this)
        val hashKey = primarySchema.hashKeyAttribute(item)
        val sortKey = primarySchema.sortKeyAttribute?.invoke(item)

        return primarySchema.key(hashKey, sortKey)
    }

    operator fun get(hashKey: HashKey, sortKey: SortKey? = null) = dynamoDb.getItem(
        TableName = tableName,
        Key = primarySchema.key(hashKey, sortKey)
    )
        .onFailure { it.reason.throwIt() }
        .item
        ?.let(itemLens)

    fun batchGet(keys: Collection<Pair<HashKey, SortKey?>>): Sequence<Document> {
        if (keys.isEmpty()) return emptySequence()

        return keys
            .asSequence()
            .map { primarySchema.key(it.first, it.second) }
            .chunked(BATCH_GET_LIMIT)
            .flatMap { chunk ->
                val response = dynamoDb.batchGetItem(
                    mapOf(
                        tableName to ReqGetItem.Get(chunk)
                    )
                ).onFailure { it.reason.throwIt() }

                response.Responses?.get(tableName.value).orEmpty()
            }
            .map(itemLens)
    }

    fun batchSave(documents: Collection<Document>) {
        if (documents.isEmpty()) return

        for (chunk in documents.chunked(BATCH_PUT_LIMIT)) {
            val batch = chunk.map { obj ->
                val item = Item().with(itemLens of obj)
                ReqWriteItem.Put(item)
            }

            dynamoDb.batchWriteItem(mapOf(tableName to batch))
                .onFailure { it.reason.throwIt() }
        }
    }

    fun save(document: Document) {
        val item = Item().with(itemLens of document)

        dynamoDb.putItem(tableName, item)
            .onFailure { it.reason.throwIt() }
    }

    fun delete(hashKey: HashKey, sortKey: SortKey? = null) {
        dynamoDb.deleteItem(
            TableName = tableName,
            Key = primarySchema.key(hashKey, sortKey)
        )
    }

    fun delete(document: Document) {
        val item = Item().with(itemLens of document)
        return delete(
            hashKey = primarySchema.hashKeyAttribute(item),
            sortKey = primarySchema.sortKeyAttribute?.invoke(item)
        )
    }

    fun batchDelete(documents: Collection<Document>) = batchDeleteKeys(documents.map { it.key() })

    @JvmName("batchDeleteKeyPairs")
    fun batchDelete(keys: Collection<Pair<HashKey, SortKey?>>) {
        batchDeleteKeys(keys.map { primarySchema.key(it.first, it.second) })
    }

    private fun batchDeleteKeys(keys: Collection<Key>) {
        for (chunk in keys.chunked(BATCH_PUT_LIMIT)) {
            val batch = chunk.map { key ->
                ReqWriteItem.Delete(key)
            }

            dynamoDb.batchWriteItem(mapOf(tableName to batch))
                .onFailure { it.reason.throwIt() }
        }
    }

    fun <NewHashKey : Any, NewSortKey : Any> index(
        schema: DynamoDbTableMapperSchema<NewHashKey, NewSortKey>
    ) = DynamoDbIndexMapper(
        dynamoDb = dynamoDb,
        tableName = tableName,
        itemLens = itemLens,
        schema = schema
    )

    fun primaryIndex() = index(primarySchema)

    fun createTable(
        vararg secondarySchemas: DynamoDbTableMapperSchema.Secondary<*, *>
    ): Result<TableDescriptionResponse, RemoteFailure> {
        val attributeDefinitions = primarySchema.attributeDefinitions().toMutableSet()
        val globalIndexes = mutableListOf<GlobalSecondaryIndex>()
        val localIndexes = mutableListOf<LocalSecondaryIndex>()

        for (schema in secondarySchemas) {
            attributeDefinitions += schema.attributeDefinitions()
            when (schema) {
                is DynamoDbTableMapperSchema.GlobalSecondary -> globalIndexes += schema.toIndex()
                is DynamoDbTableMapperSchema.LocalSecondary -> localIndexes += schema.toIndex()
            }
        }

        return dynamoDb.createTable(
            TableName = tableName,
            KeySchema = primarySchema.keySchema(),
            AttributeDefinitions = attributeDefinitions.toList(),
            GlobalSecondaryIndexes = globalIndexes.takeIf { it.isNotEmpty() },
            LocalSecondaryIndexes = localIndexes.takeIf { it.isNotEmpty() }
        )
    }

    fun deleteTable() = dynamoDb.deleteTable(tableName)
}

inline fun <reified Document : Any, HashKey : Any, SortKey : Any> DynamoDb.tableMapper(
    TableName: TableName,
    hashKeyAttribute: Attribute<HashKey>,
    sortKeyAttribute: Attribute<SortKey>? = null,
    autoMarshalling: AutoMarshalling = DynamoDbMoshi
) = tableMapper<Document, HashKey, SortKey>(
    TableName = TableName,
    primarySchema = DynamoDbTableMapperSchema.Primary(hashKeyAttribute, sortKeyAttribute),
    autoMarshalling = autoMarshalling
)

inline fun <reified Document : Any, HashKey : Any, SortKey : Any> DynamoDb.tableMapper(
    TableName: TableName,
    primarySchema: DynamoDbTableMapperSchema.Primary<HashKey, SortKey>,
    autoMarshalling: AutoMarshalling = DynamoDbMoshi
): DynamoDbTableMapper<Document, HashKey, SortKey> {
    return DynamoDbTableMapper(
        dynamoDb = this,
        tableName = TableName,
        itemLens = autoMarshalling.autoDynamoLens(),
        primarySchema = primarySchema
    )
}
