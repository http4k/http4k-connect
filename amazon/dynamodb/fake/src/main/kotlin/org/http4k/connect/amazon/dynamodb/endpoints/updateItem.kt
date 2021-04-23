package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.action.UpdateItem
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.updateItem(tables: Storage<List<Item>>) = route<UpdateItem> { update ->
    tables[update.TableName.value]
        ?.let { existing ->
            existing.retrieve(update.Key)?.let { existingItem ->
                // something to update the values here
                val updatedItem = existingItem
                tables[update.TableName.value] = existing.filterNot { it == existingItem } + updatedItem
                ModifiedItem(updatedItem.asItemResult())
            }
        }
}

