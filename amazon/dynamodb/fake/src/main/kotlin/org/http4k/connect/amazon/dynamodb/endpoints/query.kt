package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.Query
import org.http4k.connect.amazon.dynamodb.action.QueryResponse
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.query(tables: Storage<DynamoTable>) = route<Query> { query ->
    val table = tables[query.TableName.value] ?: return@route null
    val schema = table.table.keySchema(query.IndexName)
    val comparator = schema.comparator(query.ScanIndexForward ?: true)

    val matches = table.items
        .asSequence()
        .filter(schema.filterNullKeys()) // exclude items not held by selected index
        .mapNotNull {
            it.condition(
                expression = query.KeyConditionExpression,
                expressionAttributeNames = query.ExpressionAttributeNames,
                expressionAttributeValues = query.ExpressionAttributeValues
            )
        }
        .sortedWith(comparator)  // sort by selected index
        .dropWhile {
            query.ExclusiveStartKey != null && comparator.compare(
                it,
                query.ExclusiveStartKey!!
            ) <= 0
        }  // skip previous pages
        .toList()

    val page = matches.take((query.Limit ?: table.maxPageSize).coerceAtMost(table.maxPageSize))
    val filteredPage = page.mapNotNull {
        it.condition(
            expression = query.FilterExpression,
            expressionAttributeNames = query.ExpressionAttributeNames,
            expressionAttributeValues = query.ExpressionAttributeValues
        )
    }

    QueryResponse(
        Count = filteredPage.size,
        Items = filteredPage.map { it.asItemResult() },
        LastEvaluatedKey = page.lastOrNull()
            ?.takeIf { page.size < matches.size }
            ?.let { last ->
                buildMap {
                    this += last.key(table.table.KeySchema!!)
                    if (schema != null) {
                        this += last.key(schema)
                    }
                }
            }
    )
}
