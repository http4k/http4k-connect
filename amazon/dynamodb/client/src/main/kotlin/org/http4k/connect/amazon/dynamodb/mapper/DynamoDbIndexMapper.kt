package org.http4k.connect.amazon.dynamodb.mapper

import dev.forkhandles.result4k.onFailure
import org.http4k.connect.amazon.dynamodb.*
import org.http4k.connect.amazon.dynamodb.model.*
import org.http4k.lens.BiDiLens

class DynamoDbIndexMapper<Document: Any, HashKey: Any, SortKey: Any>(
    private val dynamoDb: DynamoDb,
    private val tableName: TableName,
    private val itemLens: BiDiLens<Item, Document>,
    private val schema: DynamoDbTableMapperSchema<HashKey, SortKey>
) {
    fun scan(
        filter: String? = null,
        names: Map<String, AttributeName>? = null,
        values: Map<String, AttributeValue>? = null
    ): Sequence<Document> {
        return dynamoDb.scanPaginated(
            TableName = tableName,
            FilterExpression = filter,
            ExpressionAttributeNames = names,
            ExpressionAttributeValues = values
        ).flatMap { result ->
            result.onFailure { it.reason.throwIt() }
        }.map(itemLens)
    }

    fun scanPage(
        filter: String? = null,
        names: Map<String, AttributeName>? = null,
        values: Map<String, AttributeValue>? = null,
        exclusiveStartKey: Pair<HashKey, SortKey?>? = null,
        limit: Int? = null
    ): DynamoDbPage<Document, HashKey, SortKey> {
        val page = dynamoDb.scan(
            TableName = tableName,
            IndexName = schema.indexName,
            FilterExpression = filter,
            ExpressionAttributeNames = names,
            ExpressionAttributeValues = values,
            ExclusiveStartKey = exclusiveStartKey?.let { schema.key(exclusiveStartKey.first, exclusiveStartKey.second) },
            Limit = limit
        ).onFailure { it.reason.throwIt() }

        return DynamoDbPage(
            items = page.items.map(itemLens),
            nextHashKey = page.LastEvaluatedKey?.let { schema.hashKeyAttribute[it] },
            nextSortKey = page.LastEvaluatedKey?.let { key -> schema.sortKeyAttribute?.let { attr -> attr[key] } }
        )
    }

    fun query(
        filter: String? = null,
        names: Map<String, AttributeName>? = null,
        values: Map<String, AttributeValue>? = null,
        scanIndexForward: Boolean = true
    ): Sequence<Document> {
        return dynamoDb.queryPaginated(
            TableName = tableName,
            IndexName = schema.indexName,
            KeyConditionExpression = filter,
            ExpressionAttributeNames = names,
            ExpressionAttributeValues = values,
            ScanIndexForward = scanIndexForward
        ).flatMap { result ->
            result.onFailure { it.reason.throwIt() }
        }.map(itemLens)
    }

    fun query(hashKey: HashKey, scanIndexForward: Boolean = true): Sequence<Document> {
        return query(
            filter = "${schema.hashKeyAttribute} = :val1",
            values = mapOf(":val1" to schema.hashKeyAttribute.asValue(hashKey)),
            scanIndexForward = scanIndexForward
        )
    }

    fun queryPage(
        hashKey: HashKey,
        scanIndexForward: Boolean = true,
        exclusiveStartKey: SortKey? = null,
        limit: Int? = null
    ): DynamoDbPage<Document, HashKey, SortKey> {
        val page = dynamoDb.query(
            TableName = tableName,
            IndexName = schema.indexName,
            KeyConditionExpression = "#key1 = :val1",
            ExpressionAttributeNames = mapOf("#key1" to schema.hashKeyAttribute.name),
            ExpressionAttributeValues = mapOf(":val1" to schema.hashKeyAttribute.asValue(hashKey)),
            ScanIndexForward = scanIndexForward,
            ExclusiveStartKey = exclusiveStartKey?.let { schema.key(hashKey, exclusiveStartKey) },
            Limit = limit
        ).onFailure { it.reason.throwIt() }

        return DynamoDbPage(
            items = page.items.map(itemLens),
            nextHashKey = page.LastEvaluatedKey?.let { schema.hashKeyAttribute[it] },
            nextSortKey = page.LastEvaluatedKey?.let { key -> schema.sortKeyAttribute?.let { attr -> attr[key] } }
        )
    }
}
