package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.Query
import org.http4k.connect.amazon.dynamodb.action.QueryResponse
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.query(tables: Storage<DynamoTable>) = route<Query> { query ->
    val table = tables[query.TableName.value] ?: return@route null
    val schema = table.table.keySchema(query.IndexName)

    val filter = schema.filterNullKeys()
    val comparator = schema.comparator(query.ScanIndexForward ?: true)

    val matches = table.items
        .asSequence()
        .filter(filter)
        .mapNotNull {it.condition(
            expression = query.KeyConditionExpression,
            expressionAttributeNames = query.ExpressionAttributeNames,
            expressionAttributeValues = query.ExpressionAttributeValues
        ) }
        .mapNotNull { it.condition(
            expression = query.FilterExpression,
            expressionAttributeNames = query.ExpressionAttributeNames,
            expressionAttributeValues = query.ExpressionAttributeValues
        ) }
        .sortedWith(comparator)
        .dropWhile { query.ExclusiveStartKey != null && comparator.compare(it, query.ExclusiveStartKey!!) <= 0 }
        .toList()

    val page = matches.take((query.Limit ?: table.maxPageSize).coerceAtMost(table.maxPageSize))

    QueryResponse(
        Count = page.size,
        Items = page.map { it.asItemResult() },
        LastEvaluatedKey = if (page.size < matches.size && schema != null) {
            page.lastOrNull()?.key(schema)
        } else null
    )
}
