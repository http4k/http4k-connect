package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.GetItemsResponse
import org.http4k.connect.amazon.dynamodb.action.TransactGetItems
import org.http4k.connect.amazon.dynamodb.model.GetItemsResponseItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.transactGetItems(tables: Storage<DynamoTable>) = route<TransactGetItems> {
    synchronized(tables) {
        GetItemsResponse(
            it.TransactItems.map { get ->
                GetItemsResponseItem(
                    tables[get.Get["TableName"]!!.toString()]
                        ?.let {
                            it.retrieve(convert(get.Get["Key"]!!))?.asItemResult()
                        } ?: emptyMap()
                )
            }
        )
    }
}
