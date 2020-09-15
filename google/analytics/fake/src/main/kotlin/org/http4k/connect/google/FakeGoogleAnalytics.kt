package org.http4k.connect.google

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val DEFAULT_PORT = 30000

class FakeGoogleAnalytics : HttpHandler {
    override fun invoke(request: Request) = Response(OK).body(request.body)
}

fun main() {
    FakeGoogleAnalytics().asServer(SunHttp(DEFAULT_PORT)).start()
}
