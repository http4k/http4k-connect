package org.http4k.connect.plugin.foo

import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.PagedAction
import org.http4k.connect.plugin.TestPaged
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response

@Http4kConnectAction
data class TestPagedAction(val input: String) : FooAction<TestPaged>,
    PagedAction<String, String, TestPaged, TestPagedAction> {
    override fun next(token: String) = this

    override fun toRequest() = Request(Method.GET, "")

    override fun toResult(response: Response) = Success(TestPaged(input))
}
