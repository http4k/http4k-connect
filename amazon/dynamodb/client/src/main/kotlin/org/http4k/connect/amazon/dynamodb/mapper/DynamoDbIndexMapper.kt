package org.http4k.connect.amazon.dynamodb.mapper

import dev.forkhandles.result4k.onFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.Select
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues
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

    fun scan(
        PageSize: Int? = null,
        ConsistentRead: Boolean? = null,
        block: DynamoDbScanBuilder.() -> Unit
    ): Sequence<Document> {
        val scan = DynamoDbScanBuilder().apply(block).build()
        return scan(
            FilterExpression = scan.filterExpression,
            ExpressionAttributeNames = scan.expressionAttributeNames,
            ExpressionAttributeValues = scan.expressionAttributeValues,
            PageSize = PageSize,
            ConsistentRead = ConsistentRead
        )
    }

    fun scanPage(
        FilterExpression: String? = null,
        ExpressionAttributeNames: TokensToNames? = null,
        ExpressionAttributeValues: TokensToValues? = null,
        ExclusiveStartKey: Key? = null,
        Limit: Int? = null,
        ConsistentRead: Boolean? = null,
    ): DynamoDbPage<Document> {
        val page = dynamoDb.scan(
            TableName = tableName,
            IndexName = schema.indexName,
            FilterExpression = FilterExpression,
            ExpressionAttributeNames = ExpressionAttributeNames,
            ExpressionAttributeValues = ExpressionAttributeValues,
            ExclusiveStartKey = ExclusiveStartKey,
            Limit = Limit,
            ConsistentRead = ConsistentRead
        ).onFailure { it.reason.throwIt() }

        return DynamoDbPage(
            items = page.items.map(itemLens),
            lastEvaluatedKey = page.LastEvaluatedKey
        )
    }

    fun scanPage(
        ExclusiveStartKey: Key? = null,
        Limit: Int? = null,
        ConsistentRead: Boolean? = null,
        block: DynamoDbScanBuilder.() -> Unit
    ): DynamoDbPage<Document> {
        val scan = DynamoDbScanBuilder().apply(block).build()
        return scanPage(
            FilterExpression = scan.filterExpression,
            ExpressionAttributeNames = scan.expressionAttributeNames,
            ExpressionAttributeValues = scan.expressionAttributeValues,
            ExclusiveStartKey = ExclusiveStartKey,
            Limit = Limit,
            ConsistentRead = ConsistentRead
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

    fun query(
        ScanIndexForward: Boolean = true,
        PageSize: Int? = null,
        ConsistentRead: Boolean? = null,
        block: DynamoDbQueryBuilder<HashKey, SortKey>.() -> Unit
    ): Sequence<Document> {
        val query = DynamoDbQueryBuilder(schema).apply(block).build()
        return query(
            KeyConditionExpression = query.keyConditionExpression,
            FilterExpression = query.filterExpression,
            ExpressionAttributeNames = query.expressionAttributeNames,
            ExpressionAttributeValues = query.expressionAttributeValues,
            ScanIndexForward = ScanIndexForward,
            PageSize = PageSize,
            ConsistentRead = ConsistentRead
        )
    }

    fun queryPage(
        KeyConditionExpression: String? = null,
        FilterExpression: String? = null,
        ExpressionAttributeNames: TokensToNames? = null,
        ExpressionAttributeValues: TokensToValues? = null,
        ExclusiveStartKey: Key? = null,
        ScanIndexForward: Boolean = true,
        Limit: Int? = null,
        ConsistentRead: Boolean? = null,
    ): DynamoDbPage<Document> {
        val page = dynamoDb.query(
            TableName = tableName,
            IndexName = schema.indexName,
            KeyConditionExpression = KeyConditionExpression,
            ExpressionAttributeNames = ExpressionAttributeNames,
            ExpressionAttributeValues = ExpressionAttributeValues,
            FilterExpression = FilterExpression,
            ScanIndexForward = ScanIndexForward,
            ExclusiveStartKey = ExclusiveStartKey,
            Limit = Limit,
            ConsistentRead = ConsistentRead
        ).onFailure { it.reason.throwIt() }

        return DynamoDbPage(
            items = page.items.map(itemLens),
            lastEvaluatedKey = page.LastEvaluatedKey
        )
    }

    fun queryPage(
        HashKey: HashKey,
        ScanIndexForward: Boolean = true,
        ExclusiveStartKey: Key? = null,
        Limit: Int? = null,
        ConsistentRead: Boolean? = null,
    ): DynamoDbPage<Document> = queryPage(
        KeyConditionExpression = "#key1 = :val1",
        ExpressionAttributeNames = mapOf("#key1" to schema.hashKeyAttribute.name),
        ExpressionAttributeValues = mapOf(":val1" to schema.hashKeyAttribute.asValue(HashKey)),
        ScanIndexForward = ScanIndexForward,
        ExclusiveStartKey = ExclusiveStartKey,
        Limit = Limit,
        ConsistentRead = ConsistentRead
    )

    fun queryPage(
        ScanIndexForward: Boolean = true,
        Limit: Int? = null,
        ConsistentRead: Boolean? = null,
        ExclusiveStartKey: Key? = null,
        block: DynamoDbQueryBuilder<HashKey, SortKey>.() -> Unit
    ): DynamoDbPage<Document> {
        val query = DynamoDbQueryBuilder(schema).apply(block).build()
        return queryPage(
            KeyConditionExpression = query.keyConditionExpression,
            FilterExpression = query.filterExpression,
            ExpressionAttributeNames = query.expressionAttributeNames,
            ExpressionAttributeValues = query.expressionAttributeValues,
            ExclusiveStartKey = ExclusiveStartKey,
            ScanIndexForward = ScanIndexForward,
            Limit = Limit,
            ConsistentRead = ConsistentRead
        )
    }

    fun count(
        KeyConditionExpression: String? = null,
        FilterExpression: String? = null,
        ExpressionAttributeNames: TokensToNames? = null,
        ExpressionAttributeValues: TokensToValues? = null,
        ConsistentRead: Boolean? = null,
    ): Int {
        var count = 0
        var startKey: Key? = null
        if (KeyConditionExpression == null) {
            do {
                val response = dynamoDb.scan(
                    TableName = tableName,
                    IndexName = schema.indexName,
                    FilterExpression = FilterExpression,
                    ExpressionAttributeNames = ExpressionAttributeNames,
                    ExpressionAttributeValues = ExpressionAttributeValues,
                    ExclusiveStartKey = startKey,
                    ConsistentRead = ConsistentRead,
                    Select = Select.COUNT,
                ).onFailure { it.reason.throwIt() }
                startKey = response.LastEvaluatedKey
                count += response.Count
            } while (startKey != null)
        } else {
            do {
                val response = dynamoDb.query(
                    TableName = tableName,
                    IndexName = schema.indexName,
                    KeyConditionExpression = KeyConditionExpression,
                    FilterExpression = FilterExpression,
                    ExpressionAttributeNames = ExpressionAttributeNames,
                    ExpressionAttributeValues = ExpressionAttributeValues,
                    ExclusiveStartKey = startKey,
                    ConsistentRead = ConsistentRead,
                    Select = Select.COUNT,
                ).onFailure { it.reason.throwIt() }
                startKey = response.LastEvaluatedKey
                count += response.Count
            } while (startKey != null)
        }
        return count
    }

    fun count(
        ConsistentRead: Boolean? = null,
        block: DynamoDbQueryBuilder<HashKey, SortKey>.() -> Unit
    ): Int {
        val query = DynamoDbQueryBuilder(schema).apply(block).build()
        return count(
            KeyConditionExpression = query.keyConditionExpression,
            FilterExpression = query.filterExpression,
            ExpressionAttributeNames = query.expressionAttributeNames,
            ExpressionAttributeValues = query.expressionAttributeValues,
            ConsistentRead = ConsistentRead
        )
    }
}
