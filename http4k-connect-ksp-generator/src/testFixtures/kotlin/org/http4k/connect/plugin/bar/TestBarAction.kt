package org.http4k.connect.plugin.bar

import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.plugin.foo.FooAction
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response

@Http4kConnectAction
data class TestBarAction(val input: String, val input2: String) : BarAction<String> {
    constructor(input: String) : this(input, input)

    override fun toRequest() = Request(Method.GET, "")

    override fun toResult(response: Response) = Success(input)
}
