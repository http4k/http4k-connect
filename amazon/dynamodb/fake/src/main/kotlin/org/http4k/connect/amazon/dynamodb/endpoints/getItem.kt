package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.action.GetItem
import org.http4k.connect.amazon.dynamodb.action.GetResponse
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.getItem(tables: Storage<List<Item>>) = route<GetItem> {
    tables[it.TableName.value]
        ?.let { existing ->
            GetResponse(
                existing.retrieve(it.Key)
                    ?.asItemResult()
            )
        }
}

fun List<Item>.retrieve(key: Key) = firstOrNull { it.toList().containsAll(key.toList()) }
