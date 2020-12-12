package org.http4k.connect.example

import dev.forkhandles.result4k.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request

fun Example.Companion.Http(httpHandler: HttpHandler) = object : Example {
    override operator fun invoke(request: Echo) =
        Success(Echoed(httpHandler(Request(Method.GET, "echo").body(request.value)).bodyString()))
}
