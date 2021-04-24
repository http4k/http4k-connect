package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.TableDefinition
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.action.UpdateTable
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.updateTable(tables: Storage<TableDefinition>) = route<UpdateTable> { update ->
    tables[update.TableName.value]
        ?.let { current ->
            val updated = current.table.copy(
                AttributeDefinitions = listOfNotNull(
                    current.table.AttributeDefinitions,
                    update.AttributeDefinitions
                ).flatten()
            )
            tables[update.TableName.value] = current.copy(table = updated)

            TableDescriptionResponse(updated)
        }
}

