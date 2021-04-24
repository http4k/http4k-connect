package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.TableDefinition
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.action.PutItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.putItem(tables: Storage<TableDefinition>) = route<PutItem> {
    tables[it.TableName.value]
        ?.let { current ->
            tables[it.TableName.value] = current.withItem(it.Item)
            ModifiedItem(it.Item.asItemResult())
        }
}
