package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.ModifiedItem
import org.http4k.connect.amazon.dynamodb.action.PutItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.putItem(tables: Storage<DynamoTable>) = route<PutItem> {
    tables[it.TableName.value]
        ?.let { table ->
            tables[it.TableName.value] = table.withItem(it.Item)
            ModifiedItem(it.Item.asItemResult())
        }
    Unit
}
