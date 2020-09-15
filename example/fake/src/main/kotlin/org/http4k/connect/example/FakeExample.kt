package org.http4k.connect.example

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val DEFAULT_PORT = 30099

class FakeExample : HttpHandler {
    override fun invoke(p1: Request) = Response(OK).body(p1.body)
}

fun main() {
    FakeExample().asServer(SunHttp(DEFAULT_PORT)).start()
}
