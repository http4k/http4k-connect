package org.http4k.connect.amazon.dynamodb

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.Paged
import org.http4k.connect.amazon.dynamodb.action.PagedAction

fun <Token, ItemType, Rsp : Paged<Token, ItemType>, Out> DynamoDb.paginate(
    original: PagedAction<Token, ItemType, Rsp>,
    fn: (ItemType) -> Out
): Sequence<Out> {
    var nextRequest = original
    var done = false

    return generateSequence {
        when {
            done -> null
            else -> when (val result = this(nextRequest)) {
                is Success -> {
                    with(result.value) {
                        token()?.also { nextRequest = nextRequest.next(it) } ?: run { done = true }
                        items.map(fn)
                    }
                }
                is Failure -> error(result.reason.message ?: result.reason.toString())
            }
        }
    }.flatten()
}
