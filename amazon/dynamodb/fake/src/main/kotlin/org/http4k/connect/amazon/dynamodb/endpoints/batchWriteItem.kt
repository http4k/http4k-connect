package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.BatchWriteItem
import org.http4k.connect.amazon.dynamodb.action.BatchWriteItems
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.batchWriteItem(tables: Storage<DynamoTable>) = route<BatchWriteItem> {
    it.RequestItems
        .forEach { (tableName, writeItems) ->
            tables[tableName.value]
                ?.let { table ->
                    writeItems
                        .map {
                            tables[tableName.value] = when {
                                it.PutRequest != null -> table.withItem(it.PutRequest!!["Item"]!!)
                                it.DeleteRequest != null -> table.withoutItem(it.DeleteRequest!!["Key"]!!)
                                else -> table
                            }
                        }
                }
        }
    BatchWriteItems()
}
