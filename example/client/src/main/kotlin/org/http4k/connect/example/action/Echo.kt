package org.http4k.connect.example.action

import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response

@Http4kConnectAction
data class Echo(val value: String) : ExampleAction<Echoed> {
    override fun toRequest() = Request(GET, "echo").body(value)

    override fun toResult(response: Response) = Success(Echoed(response.bodyString()))
}

data class Echoed(val value: String)
