package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.PutItem
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.putItem(tables: Storage<DynamoTable>) = route<PutItem> {
    tables.putItem(it.TableName, it.Item)
}
