package org.http4k.connect.amazon.dynamodb.mapper

import dev.forkhandles.result4k.onFailure
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.dynamodb.*
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.model.*
import org.http4k.lens.BiDiLens
import dev.forkhandles.result4k.Result

class DynamoDbTableMapper<Document: Any, HashKey: Any, SortKey: Any>(
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

    operator fun get(hashKey: HashKey, sortKey: SortKey? = null): Document? {
        return dynamoDb.getItem(
            TableName = tableName,
            Key = primarySchema.key(hashKey, sortKey)
        )
            .onFailure { it.reason.throwIt() }
            .item
            ?.let(itemLens)
    }

    operator fun plusAssign(achievements: Collection<Document>) {
        return dynamoDb.batchPutItems(tableName, achievements, itemLens)
            .onFailure { it.reason.throwIt() }
    }

    operator fun plusAssign(document: Document) {
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

    operator fun minusAssign(document: Document) {
        val item = Item().with(itemLens of document)
        return delete(
            hashKey = primarySchema.hashKeyAttribute(item),
            sortKey = primarySchema.sortKeyAttribute?.invoke(item)
        )
    }

    operator fun minusAssign(documents: Collection<Document>) {
        val keys = documents.map { it.key() }

        dynamoDb.batchDeleteItems(
            TableName = tableName,
            keys = keys
        ).onFailure { it.reason.throwIt() }
    }

    fun <NewHashKey: Any, NewSortKey: Any> index(
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
        val localIndexes = mutableListOf<LocalSecondaryIndexes>()

        for (schema in secondarySchemas) {
            attributeDefinitions += schema.attributeDefinitions()
            when(schema) {
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

    fun deleteTable(): Result<TableDescriptionResponse, RemoteFailure> {
        return dynamoDb.deleteTable(tableName)
    }
}
