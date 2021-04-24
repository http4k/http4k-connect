package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.TableDefinition
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.action.UpdateItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.updateItem(tables: Storage<TableDefinition>) = route<UpdateItem> { update ->
    tables[update.TableName.value]
        ?.let { current ->
            current.retrieve(update.Key)?.let { existingItem ->
                // something to update the values here
                val updatedItem = existingItem
                tables[update.TableName.value] = current.withItem(updatedItem)
                ModifiedItem(updatedItem.asItemResult())
            }
        }
}

