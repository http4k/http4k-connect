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

    fun query(
        filter: String? = null,
        names: Map<String, AttributeName>? = null,
        values: Map<String, AttributeValue>? = null,
        scanIndexForward: Boolean = true
    ): Sequence<Document> {
        return dynamoDb.queryPaginated(
            TableName = tableName,
            IndexName = schema.indexName,
            FilterExpression = filter,
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
}
