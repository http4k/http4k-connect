package org.http4k.connect.amazon.dynamodb

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.dynamodb.action.Item
import org.http4k.connect.amazon.dynamodb.action.Paged
import org.http4k.connect.amazon.dynamodb.action.PagedAction

fun <T, R : Paged> DynamoDb.paginate(original: PagedAction<R>, fn: (Item) -> T): Sequence<T> {
    var nextRequest = original
    var done = false

    return generateSequence {
        when {
            done -> null
            else -> when (val result = this(nextRequest)) {
                is Success -> {
                    with(result.value) {
                        LastEvaluatedKey
                            ?.also {
                                nextRequest = nextRequest.next(it)
                            } ?: run {
                            done = true
                        }
                        items.map(fn)
                    }
                }
                is Failure -> error(result.reason.message ?: result.reason.toString())
            }
        }
    }.flatten()
}
