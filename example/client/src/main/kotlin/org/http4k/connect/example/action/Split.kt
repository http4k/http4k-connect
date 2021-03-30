package org.http4k.connect.example.action

import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.Paged
import org.http4k.connect.amazon.PagedAction
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response

@Http4kConnectAction
data class Split(val value: String, val token: Int? = null) : ExampleAction<SplitChunk>,
    PagedAction<Int, Char, SplitChunk, Split> {
    override fun toRequest() = Request(POST, "/echo").body(value)

    override fun toResult(response: Response): Success<SplitChunk> {
        val items = response.bodyString().split(":")
        return Success(SplitChunk(items[0].toList(), items[1].takeIf { it.isNotEmpty() }?.toInt()))
    }

    override fun next(token: Int) = copy(token = token)
}

data class SplitChunk(override val items: List<Char>, val index: Int?) : Paged<Int, Char> {
    override fun token() = index
}
