package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.BatchWriteItem
import org.http4k.connect.amazon.dynamodb.action.BatchWriteItems
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.batchWriteItem(tables: Storage<DynamoTable>) = route<BatchWriteItem> {
    BatchWriteItems(
        it.RequestItems
            .mapNotNull { (table, writeItems) ->
                tables[table.value]
                    ?.let {
                        writeItems
                            .mapNotNull {
                                when {
                                    it.PutRequest != null -> null
                                    it.DeleteRequest != null -> ""
                                    else -> null
                                }
                            }
                        table to emptyMap<String, AttributeName>()
                    }
            }
            .toMap()
    )
}
