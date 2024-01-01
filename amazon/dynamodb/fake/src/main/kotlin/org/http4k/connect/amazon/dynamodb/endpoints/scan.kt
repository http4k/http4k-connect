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
        .filter(schema.filterNullKeys())  // exclude items not held by selected index
        .sortedWith(comparator)  // sort by selected index
        .dropWhile { scan.ExclusiveStartKey != null && comparator.compare(it, scan.ExclusiveStartKey!!) <= 0 }   // skip previous pages
        .toList()

    val page = matches.take((scan.Limit ?: table.maxPageSize).coerceAtMost(table.maxPageSize))
    val filteredPage = page.mapNotNull { it.condition(
        expression = scan.FilterExpression,
        expressionAttributeNames = scan.ExpressionAttributeNames,
        expressionAttributeValues = scan.ExpressionAttributeValues
    ) }

    ScanResponse(
        Count = filteredPage.size,
        Items = filteredPage.map { it.asItemResult() },
        LastEvaluatedKey = if (page.size < matches.size && schema != null) {
            val lastItem = page.lastOrNull()
            val indexKey = lastItem?.key(schema)
            val primaryKey = lastItem?.key(table.table.KeySchema!!)
            (indexKey.orEmpty() + primaryKey.orEmpty()).takeIf { it.isNotEmpty() }
        } else null
    )
}
