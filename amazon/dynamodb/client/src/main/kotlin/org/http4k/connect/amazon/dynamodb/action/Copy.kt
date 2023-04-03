package org.http4k.connect.amazon.dynamodb.action

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.valueOrNull
import org.http4k.connect.Paged
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.batchWriteItem
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.ReqWriteItem
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.paginated

/**
 * Copies items between into another table, using BatchWriteItem to insert.
 * Returns the Unprocessed Items from the last insert if there were any.
 */
fun <R : Paged<Key, Item>, Self : DynamoDbPagedAction<R, Self>> DynamoDb.copy(
    action: Self, destination: TableName, mappingFn: (Item) -> Item
): Result<Map<String, ReqWriteItem>?, RemoteFailure> = paginated(::invoke, action)
    .map { it.flatMap { batchWriteItem(mapOf(destination to it.map(mappingFn).map { ReqWriteItem.Put(it) })) } }
    .takeWhile { it.valueOrNull() == null || it.valueOrNull()!!.UnprocessedItems != null }
    .lastOrNull()
    ?.map {
        it.UnprocessedItems
    }
    ?: Success(null)
