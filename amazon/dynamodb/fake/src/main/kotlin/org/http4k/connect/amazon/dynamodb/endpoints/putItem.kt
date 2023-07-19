package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.JsonError
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.action.PutItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.putItem(tables: Storage<DynamoTable>) = route<PutItem> { req ->
    val table = tables[req.TableName.value] ?: return@route null

    if (req.ConditionExpression != null) {
        val existing = table.retrieve(req.Item.key(table.table.KeySchema!!))
        if (existing != null) {
            existing.condition(
                expression = req.ConditionExpression,
                expressionAttributeNames = req.ExpressionAttributeNames,
                expressionAttributeValues = req.ExpressionAttributeValues
            ) ?: return@route JsonError(
                "com.amazonaws.dynamodb.v20120810#ConditionalCheckFailedException",
                "The conditional request failed"
            )
        }
    }

    tables[req.TableName.value] = table.withItem(req.Item)
    ModifiedItem(req.Item.asItemResult())

    Unit
}
