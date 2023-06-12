package org.http4k.connect.plugin.foo

import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.PagedAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.plugin.TestPaged
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response

@Http4kConnectAction
data class TestPagedAction(val input: String) : FooAction<TestPaged>,
    PagedAction<String, String, TestPaged, TestPagedAction> {
    override fun next(token: String) = this

    override fun toRequest() = Request(GET, "")

    override fun toResult(response: Response): Result4k<TestPaged, RemoteFailure> = Success(TestPaged(input))
}
