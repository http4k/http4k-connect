package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.TableDefinition
import org.http4k.connect.amazon.dynamodb.action.GetItem
import org.http4k.connect.amazon.dynamodb.action.GetResponse
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.getItem(tables: Storage<TableDefinition>) = route<GetItem> {
    tables[it.TableName.value]
        ?.let { current ->
            GetResponse(
                current.retrieve(it.Key)
                    ?.asItemResult()
            )
        }
}
