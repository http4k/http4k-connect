package org.http4k.connect.amazon.dynamodb

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.Paged
import org.http4k.connect.amazon.dynamodb.action.PagedAction

/**
 * Paginate the response of the passed action
 */
fun <Token, ItemType, Rsp : Paged<Token, ItemType>> DynamoDb.paginate(action: PagedAction<Token, ItemType, Rsp>)
    : Sequence<ItemType> {
    var nextRequest = action
    var done = false

    return generateSequence {
        when {
            done -> null
            else -> when (val result = this(nextRequest)) {
                is Success -> with(result.value) {
                    token()?.also { nextRequest = nextRequest.next(it) } ?: run { done = true }
                    items
                }
                is Failure -> error(result.reason.message ?: result.reason.toString())
            }
        }
    }.flatten()
}
