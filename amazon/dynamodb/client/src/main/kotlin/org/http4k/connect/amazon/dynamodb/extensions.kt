package org.http4k.connect.amazon.dynamodb

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.map
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.Paged
import org.http4k.connect.amazon.dynamodb.action.PagedAction

/**
 * Paginate the response of the passed action
 */
fun <Token, ItemType, Rsp : Paged<Token, ItemType>> DynamoDb.paginate(action: PagedAction<Token, ItemType, Rsp>)
    : Sequence<Result<List<ItemType>, RemoteFailure>> {
    var nextRequest = action
    var done = false

    return generateSequence {
        when {
            done -> null
            else -> this(nextRequest).map {
                it.token()?.also { nextRequest = nextRequest.next(it) } ?: run { done = true }
                it.items
            }
        }
    }
}
