package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.BatchGetItem
import org.http4k.connect.amazon.dynamodb.action.BatchGetItems
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.batchGetItem(tables: Storage<DynamoTable>) = route<BatchGetItem> {
    BatchGetItems(it.RequestItems.flatMap { (table, get) ->
        get.Keys.mapNotNull {
            tables.getItemByKey(table, it)?.item?.let { table.value to it }
        }
    }
        .groupBy { it.first }
        .mapValues { it.value.map { it.second } })
}
