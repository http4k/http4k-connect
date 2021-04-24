package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.TableDefinition
import org.http4k.connect.amazon.dynamodb.action.DeleteTable
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.model.TableStatus.DELETING
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.deleteTable(tables: Storage<TableDefinition>) = route<DeleteTable> {
    tables[it.TableName.value]?.let { current ->
        tables.remove(current.table.TableName!!.value)
        TableDescriptionResponse(current.table.copy(TableStatus = DELETING))
    }
}
