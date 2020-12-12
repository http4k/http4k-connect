package org.http4k.connect.example

import dev.forkhandles.result4k.Success
import org.http4k.connect.Action
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response

interface ExampleAction<R> : Action<R>

data class Echo(val value: String) : ExampleAction<Echoed> {
    override fun toRequest() = Request(GET, "echo").body(value)

    override fun toResult(r: Response) = Success(Echoed(r.bodyString()))
}

data class Echoed(val value: String)

