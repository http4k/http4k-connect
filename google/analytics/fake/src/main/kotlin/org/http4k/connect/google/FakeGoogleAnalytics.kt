package org.http4k.connect.google

import org.http4k.connect.fake.ChaosFake
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val DEFAULT_PORT = 30000

class FakeGoogleAnalytics : ChaosFake() {
    override val app = routes(
        "/collect" bind POST to { Response(OK).body(it.body) }
    )
}

fun main() {
    FakeGoogleAnalytics().asServer(SunHttp(DEFAULT_PORT)).start()
}
