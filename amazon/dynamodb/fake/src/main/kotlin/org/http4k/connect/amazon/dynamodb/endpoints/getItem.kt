package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.TableDefinition
import org.http4k.connect.amazon.dynamodb.action.GetItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.getItem(tables: Storage<TableDefinition>) = route<GetItem> {
    val tableName = it.TableName
    val key = it.Key
    tables.getItemByKey(tableName, key)
}
