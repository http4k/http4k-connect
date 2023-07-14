package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.Scan
import org.http4k.connect.amazon.dynamodb.action.ScanResponse
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.scan(tables: Storage<DynamoTable>) = route<Scan> { scan ->
    val table = tables[scan.TableName.value] ?: return@route null
    val schema = table.table.keySchema(scan.IndexName)

    val comparator = schema.comparator(true)

    val matches = table.items
        .asSequence()
        .mapNotNull {
            it.condition(
                expression = scan.FilterExpression,
                expressionAttributeNames = scan.ExpressionAttributeNames,
                expressionAttributeValues = scan.ExpressionAttributeValues
            )
        }
        .sortedWith(comparator)
        .dropWhile { scan.ExclusiveStartKey != null && comparator.compare(it, scan.ExclusiveStartKey!!) <= 0 }
        .toList()

    val page = matches.take((scan.Limit ?: table.maxPageSize).coerceAtMost(table.maxPageSize))

    ScanResponse(
        Count = page.size,
        Items = page.map { it.asItemResult() },
        LastEvaluatedKey = if (page.size < matches.size && schema != null) {
            page.lastOrNull()?.key(schema)
        } else null
    )
}
