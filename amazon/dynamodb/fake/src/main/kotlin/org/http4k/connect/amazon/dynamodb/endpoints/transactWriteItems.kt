package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.TransactWriteItems
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.transactWriteItems(tables: Storage<DynamoTable>) = route<TransactWriteItems> {
    synchronized(tables) {
        it.TransactItems.forEach { write ->
            // todo
        }
        null
    }
}
