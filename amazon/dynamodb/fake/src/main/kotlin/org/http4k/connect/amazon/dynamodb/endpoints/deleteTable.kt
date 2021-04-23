package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.action.DeleteTable
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.TableDescription
import org.http4k.connect.amazon.dynamodb.model.TableStatus.DELETING
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.deleteTable(tables: Storage<List<Item>>) = route<DeleteTable> {
    tables[it.TableName.value] = mutableListOf()

    TableDescriptionResponse(
        TableDescription(
            ItemCount = 0,
            TableId = it.TableName.value,
            TableName = it.TableName,
            TableSizeBytes = 0,
            TableStatus = DELETING
        )
    )
}
