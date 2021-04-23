package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.action.CreateTable
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.TableDescription
import org.http4k.connect.amazon.dynamodb.model.TableStatus
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.createTable(tables: Storage<List<Item>>) = route<CreateTable> {
    tables[it.TableName.value] = mutableListOf()

    TableDescriptionResponse(
        TableDescription(
            AttributeDefinitions = it.AttributeDefinitions,
            ItemCount = 0,
            KeySchema = it.KeySchema,
            TableId = it.TableName.value,
            TableName = it.TableName,
            TableSizeBytes = 0,
            TableStatus = TableStatus.ACTIVE
        )
    )
}
