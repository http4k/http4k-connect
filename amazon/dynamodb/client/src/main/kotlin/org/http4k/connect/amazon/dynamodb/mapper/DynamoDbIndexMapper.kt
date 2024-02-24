package org.http4k.connect.amazon.dynamodb.mapper

import dev.forkhandles.result4k.onFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.query
import org.http4k.connect.amazon.dynamodb.queryPaginated
import org.http4k.connect.amazon.dynamodb.scan
import org.http4k.connect.amazon.dynamodb.scanPaginated
import org.http4k.lens.BiDiLens

class DynamoDbIndexMapper<Document : Any, HashKey : Any, SortKey : Any>(
    private val dynamoDb: DynamoDb,
    private val tableName: TableName,
    private val itemLens: BiDiLens<Item, Document>,
    private val schema: DynamoDbTableMapperSchema<HashKey, SortKey>
) {
    fun scan(
        FilterExpression: String? = null,
        ExpressionAttributeNames: TokensToNames? = null,
        ExpressionAttributeValues: TokensToValues? = null,
        PageSize: Int? = null,
        ConsistentRead: Boolean? = null,
    ) = dynamoDb
        .scanPaginated(
            TableName = tableName,
            FilterExpression = FilterExpression,
            ExpressionAttributeNames = ExpressionAttributeNames,
            ExpressionAttributeValues = ExpressionAttributeValues,
            Limit = PageSize,
            ConsistentRead = ConsistentRead
        )
        .flatMap { result -> result.onFailure { it.reason.throwIt() } }
        .map(itemLens)

    fun scanPage(
        FilterExpression: String? = null,
        ExpressionAttributeNames: TokensToNames? = null,
        ExpressionAttributeValues: TokensToValues? = null,
        ExclusiveStartKey: Pair<HashKey, SortKey?>? = null,
        Limit: Int? = null,
        ConsistentRead: Boolean? = null,
    ): DynamoDbPage<Document, HashKey, SortKey> {
        val page = dynamoDb.scan(
            TableName = tableName,
            IndexName = schema.indexName,
            FilterExpression = FilterExpression,
            ExpressionAttributeNames = ExpressionAttributeNames,
            ExpressionAttributeValues = ExpressionAttributeValues,
            ExclusiveStartKey = ExclusiveStartKey?.let {
                schema.key(
                    ExclusiveStartKey.first,
                    ExclusiveStartKey.second
                )
            },
            Limit = Limit,
            ConsistentRead = ConsistentRead
        ).onFailure { it.reason.throwIt() }

        return DynamoDbPage(
            items = page.items.map(itemLens),
            nextHashKey = page.LastEvaluatedKey?.let { schema.hashKeyAttribute[it] },
            nextSortKey = page.LastEvaluatedKey?.let { key -> schema.sortKeyAttribute?.let { attr -> attr[key] } }
        )
    }

    fun query(
        KeyConditionExpression: String? = null,
        FilterExpression: String? = null,
        ExpressionAttributeNames: TokensToNames? = null,
        ExpressionAttributeValues: TokensToValues? = null,
        ScanIndexForward: Boolean = true,
        PageSize: Int? = null,
        ConsistentRead: Boolean? = null,
    ) = dynamoDb
        .queryPaginated(
            TableName = tableName,
            IndexName = schema.indexName,
            KeyConditionExpression = KeyConditionExpression,
            FilterExpression = FilterExpression,
            ExpressionAttributeNames = ExpressionAttributeNames,
            ExpressionAttributeValues = ExpressionAttributeValues,
            ScanIndexForward = ScanIndexForward,
            Limit = PageSize,
            ConsistentRead = ConsistentRead
        )
        .flatMap { result -> result.onFailure { it.reason.throwIt() } }
        .map(itemLens)

    fun query(
        hashKey: HashKey,
        ScanIndexForward: Boolean = true,
        PageSize: Int? = null,
        ConsistentRead: Boolean? = null,
    ) = query(
        KeyConditionExpression = "#key1 = :val1",
        ExpressionAttributeNames = mapOf("#key1" to schema.hashKeyAttribute.name),
        ExpressionAttributeValues = mapOf(":val1" to schema.hashKeyAttribute.asValue(hashKey)),
        ScanIndexForward = ScanIndexForward,
        PageSize = PageSize,
        ConsistentRead = ConsistentRead
    )

    fun queryPage(
        KeyConditionExpression: String? = null,
        FilterExpression: String? = null,
        ExpressionAttributeNames: TokensToNames? = null,
        ExpressionAttributeValues: TokensToValues? = null,
        ExclusiveStartHashKey: HashKey? = null,
        ExclusiveStartSortKey: SortKey? = null,
        ScanIndexForward: Boolean = true,
        Limit: Int? = null,
        ConsistentRead: Boolean? = null,
    ): DynamoDbPage<Document, HashKey, SortKey> {
        val page = dynamoDb.query(
            TableName = tableName,
            IndexName = schema.indexName,
            KeyConditionExpression = KeyConditionExpression,
            ExpressionAttributeNames = ExpressionAttributeNames,
            ExpressionAttributeValues = ExpressionAttributeValues,
            FilterExpression = FilterExpression,
            ScanIndexForward = ScanIndexForward,
            ExclusiveStartKey = ExclusiveStartHashKey?.let {
                ExclusiveStartSortKey?.let {
                    schema.key(ExclusiveStartHashKey, ExclusiveStartSortKey)
                }
            },
            Limit = Limit,
            ConsistentRead = ConsistentRead
        ).onFailure { it.reason.throwIt() }

        return DynamoDbPage(
            items = page.items.map(itemLens),
            nextHashKey = page.LastEvaluatedKey?.let { schema.hashKeyAttribute[it] },
            nextSortKey = page.LastEvaluatedKey?.let { key -> schema.sortKeyAttribute?.let { attr -> attr[key] } }
        )
    }

    fun queryPage(
        HashKey: HashKey,
        ScanIndexForward: Boolean = true,
        ExclusiveStartKey: SortKey? = null,
        Limit: Int? = null,
        ConsistentRead: Boolean? = null,
    ): DynamoDbPage<Document, HashKey, SortKey> = queryPage(
        KeyConditionExpression = "#key1 = :val1",
        ExpressionAttributeNames = mapOf("#key1" to schema.hashKeyAttribute.name),
        ExpressionAttributeValues = mapOf(":val1" to schema.hashKeyAttribute.asValue(HashKey)),
        ScanIndexForward = ScanIndexForward,
        ExclusiveStartHashKey = HashKey,
        ExclusiveStartSortKey = ExclusiveStartKey,
        Limit = Limit,
        ConsistentRead = ConsistentRead
    )
}
