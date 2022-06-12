package org.http4k.connect

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.map
import org.http4k.core.Response
import org.http4k.format.AutoMarshalling
import org.http4k.lens.LensExtractor
import kotlin.reflect.KClass

/**
 * Represents a Paged response
 */
interface Paged<Token, ItemType> : Iterable<ItemType> {
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

/**
 * Superclass for paged actions where the response can be automarshalled
 */
abstract class AutomarshalledPagedAction<Token, ItemType : Any, PageType : Paged<Token, ItemType>>(
    private val toResult: (List<ItemType>, Token?) -> PageType,
    private val autoMarshalling: AutoMarshalling,
    private val kClass: KClass<PageType>
) : PagedAction<Token, ItemType, PageType, AutomarshalledPagedAction<Token, ItemType, PageType>>,
    LensExtractor<Response, Token?> {

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(
                toResult(
                    autoMarshalling
                        .asA(
                            """{"items":${bodyString()}}""".trimIndent(),
                            kClass
                        ).items,
                    this@AutomarshalledPagedAction(this)
                )
            )
            else -> Failure(RemoteFailure(toRequest().method, toRequest().uri, status, bodyString()))
        }
    }

    override fun equals(other: Any?) = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        else -> toRequest() == (other as Action<*>).toRequest()
    }

    override fun hashCode() = toRequest().hashCode()
}
