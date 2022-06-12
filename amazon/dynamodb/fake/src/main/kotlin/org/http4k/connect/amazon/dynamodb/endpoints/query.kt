package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.Query
import org.http4k.connect.amazon.dynamodb.action.QueryResponse
import org.http4k.connect.amazon.dynamodb.model.KeyType
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.query(tables: Storage<DynamoTable>) = route<Query> { query ->
    val table = tables[query.TableName.value] ?: return@route null

    val comparator = table.keySchema(query.IndexName)
        ?.find { it.KeyType == KeyType.RANGE }
        ?.AttributeName
        .comparator(query.ScanIndexForward ?: true)

    val items = table.items
        .asSequence()
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
        .map { it.asItemResult() }
        .take(query.Limit ?: Int.MAX_VALUE)
        .toList()  // TODO pagination

    QueryResponse(
        Count = items.size,
        Items = items
    )
}

