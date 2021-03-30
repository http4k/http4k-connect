package org.http4k.connect

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.map

/**
 * Represents a Paged response
 */
interface Paged<Token, ItemType>: Iterable<ItemType> {
    fun token(): Token?
    val items: List<ItemType>
    override fun iterator() = items.iterator()
}

/**
 * Superclass for all Paged actions
 */
interface PagedAction<Token, ItemType, Rsp : Paged<Token, ItemType>, Self : PagedAction<Token, ItemType, Rsp, Self>> :
    Action<Result<Rsp, RemoteFailure>> {
    fun next(token: Token): Self
}

/**
 * Paginate the response of the passed action
 */
fun <Token, ItemType, Action : PagedAction<Token, ItemType, Rsp, Action>, Rsp : Paged<Token, ItemType>> paginated(
    fn: (Action) -> Result<Rsp, RemoteFailure>, action: Action
): Sequence<Result<List<ItemType>, RemoteFailure>> {

    var nextRequest: Action? = action

    return generateSequence {
        nextRequest
            ?.let {
                fn(it).map { rsp ->
                    nextRequest = rsp.token()?.let(it::next)
                    rsp.items
                }
            }
    }
}
