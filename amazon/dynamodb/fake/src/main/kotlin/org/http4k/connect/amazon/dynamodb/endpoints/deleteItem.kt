package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.DeleteItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.deleteItem(tables: Storage<DynamoTable>) = route<DeleteItem> {
    val tableName = it.TableName
    tables[tableName.value]?.let { existing ->
        tables[tableName.value] = existing.withoutItem(it.Key)
    }
}
