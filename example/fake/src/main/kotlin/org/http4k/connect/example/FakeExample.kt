package org.http4k.connect.example

import org.http4k.connect.fake.ChaosFake
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val DEFAULT_PORT = 30099

class FakeExample : ChaosFake() {
    override val app = { req: Request -> Response(OK).body(req.body) }
}

fun main() {
    FakeExample().asServer(SunHttp(DEFAULT_PORT)).start()
}
