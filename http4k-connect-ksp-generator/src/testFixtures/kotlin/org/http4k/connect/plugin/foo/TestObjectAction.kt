package org.http4k.connect.plugin.foo

import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response

@Http4kConnectAction
object TestObjectAction : FooAction<String> {
    override fun toRequest() = Request(Method.GET, "")
    override fun toResult(response: Response) = Success("")
}
