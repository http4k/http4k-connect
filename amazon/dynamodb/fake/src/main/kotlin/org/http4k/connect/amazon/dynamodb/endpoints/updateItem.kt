package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.action.UpdateItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.updateItem(tables: Storage<DynamoTable>) = route<UpdateItem> { update ->
    val table = tables[update.TableName.value] ?: return@route null
    val existingItem = table.retrieve(update.Key) ?: update.Key

    val updated = existingItem.update(
        expression = update.UpdateExpression,
        expressionAttributeNames = update.ExpressionAttributeNames,
        expressionAttributeValues = update.ExpressionAttributeValues
    )

    tables[update.TableName.value] = table.withoutItem(existingItem).withItem(updated)
    ModifiedItem(updated.asItemResult())
}

