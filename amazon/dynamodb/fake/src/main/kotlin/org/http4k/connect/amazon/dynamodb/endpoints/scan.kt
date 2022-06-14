package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.Scan
import org.http4k.connect.amazon.dynamodb.action.ScanResponse
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.scan(tables: Storage<DynamoTable>) = route<Scan> { scan ->
    val table = tables[scan.TableName.value] ?: return@route null

    val items = table.items
        .asSequence()
        .mapNotNull {it.condition(
            expression = scan.FilterExpression,
            expressionAttributeNames = scan.ExpressionAttributeNames,
            expressionAttributeValues = scan.ExpressionAttributeValues
        ) }
        .map { it.asItemResult() }
        .take(scan.Limit ?: Int.MAX_VALUE)
        .toList()

    ScanResponse(
        Count = items.size,
        Items = items
    )
}
