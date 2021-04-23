package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.action.PutItem
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.putItem(tables: Storage<List<Item>>) = route<PutItem> {
    tables[it.TableName.value]
        ?.let { existing ->
            tables[it.TableName.value] = existing + it.Item

            ModifiedItem(
                it.Item.asItemResult()
            )
        }
}

