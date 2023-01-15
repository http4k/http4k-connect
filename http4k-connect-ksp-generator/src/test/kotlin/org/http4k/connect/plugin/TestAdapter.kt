package org.http4k.connect.plugin

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.connect.Action
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.Paged
import org.http4k.connect.PagedAction
import org.http4k.connect.RemoteFailure
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

@Http4kConnectAdapter
interface TestAdapter {
    operator fun <R> invoke(action: FooAction<R>): Result<R, RemoteFailure>

    companion object
}

fun TestAdapter.Companion.Impl() = object : TestAdapter {
    override fun <R> invoke(action: FooAction<R>) = action.toResult(Response(Status.OK))
}

interface FooAction<R> : Action<Result<R, RemoteFailure>>

@Http4kConnectAction
data class TestAction(val input: String) : FooAction<String> {
    override fun toRequest() = Request(Method.GET, "")

    override fun toResult(response: Response) = Success(input)
}

@Http4kConnectAction
data class TestPagedAction(val input: String) : FooAction<TestPaged>,
    PagedAction<String, String, TestPaged, TestPagedAction> {
    override fun next(token: String) = this

    override fun toRequest() = Request(Method.GET, "")

    override fun toResult(response: Response) = Success(TestPaged(input))
}

data class TestPaged(val token: String) : Paged<String, String> {
    override fun token() = token

    override val items = emptyList<String>()
}
