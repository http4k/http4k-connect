package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi.convert
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.DeleteItem
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.action.ModifiedItems
import org.http4k.connect.amazon.dynamodb.action.PutItem
import org.http4k.connect.amazon.dynamodb.action.TransactWriteItems
import org.http4k.connect.amazon.dynamodb.action.UpdateItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.transactWriteItems(tables: Storage<DynamoTable>) = route<TransactWriteItems> {
    synchronized(tables) {
        it.TransactItems.forEach { write ->
            write.Delete?.let {
                val req = convert<Map<String, Any?>, DeleteItem>(it)
                val tableName = req.TableName
                tables[tableName.value]?.let { existing ->
                    tables[tableName.value] = existing.withoutItem(req.Key)
                }
            }
            write.Put?.let {
                val req = convert<Map<String, Any?>, PutItem>(it)
                val table = tables[req.TableName.value] ?: return@route null
                tables[req.TableName.value] = table.withItem(req.Item)
                ModifiedItem(req.Item.asItemResult())
            }
            write.Update?.let {
                val req = convert<Map<String, Any?>, UpdateItem>(it)
                val table = tables[req.TableName.value] ?: return@route null
                val existingItem = table.retrieve(req.Key) ?: req.Key

                val updated = existingItem.update(
                    expression = req.UpdateExpression,
                    expressionAttributeNames = req.ExpressionAttributeNames,
                    expressionAttributeValues = req.ExpressionAttributeValues
                )
                tables[req.TableName.value] = table.withoutItem(existingItem).withItem(updated)
            }
        }
        ModifiedItems()
    }
}
