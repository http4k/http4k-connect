package org.http4k.connect.amazon.dynamodb

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.onFailure
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.dynamodb.model.*
import org.http4k.lens.BiDiLens

private const val batchSizeLimit = 25  // as defined by DynamoDB

fun <T> DynamoDb.batchPutItems(TableName: TableName, Items: Collection<T>, lens: BiDiLens<Item, T>): Result<Unit, RemoteFailure> {
    if (Items.isEmpty()) return Success(Unit)

    for (chunk in Items.chunked(batchSizeLimit)) {
        val batch = chunk.map { obj ->
            val item = Item().with(lens of obj)
            ReqWriteItem.Put(item)
        }

        batchWriteItem(mapOf(TableName to batch))
            .onFailure { return it }
    }

    return Success(Unit)
}

fun DynamoDb.batchDeleteItems(TableName: TableName, keys: Collection<Key>): Result<Unit, RemoteFailure> {
    for (chunk in keys.chunked(batchSizeLimit)) {
        val batch = chunk.map { key ->
            ReqWriteItem.Delete(key)
        }

        batchWriteItem(mapOf(TableName to batch))
            .onFailure { return it }
    }

    return Success(Unit)
}
