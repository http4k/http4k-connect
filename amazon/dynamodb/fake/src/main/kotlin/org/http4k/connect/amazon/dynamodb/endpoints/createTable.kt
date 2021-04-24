package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.CreateTable
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.model.TableDescription
import org.http4k.connect.amazon.dynamodb.model.TableStatus
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.createTable(tables: Storage<DynamoTable>) = route<CreateTable> {
    val tableDescription = TableDescription(
        AttributeDefinitions = it.AttributeDefinitions,
        ItemCount = 0,
        KeySchema = it.KeySchema,
        TableId = it.TableName.value,
        TableName = it.TableName,
        TableSizeBytes = 0,
        TableStatus = TableStatus.ACTIVE
    )
    tables[it.TableName.value] = DynamoTable(tableDescription, mutableListOf())

    TableDescriptionResponse(tableDescription)
}
