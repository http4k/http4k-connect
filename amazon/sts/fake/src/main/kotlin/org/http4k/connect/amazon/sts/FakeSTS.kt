package org.http4k.connect.amazon.sts

import org.http4k.connect.ChaosFake
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class FakeSTS : ChaosFake() {
    override val app = { req: Request -> Response(Status.OK) }
}

fun main() {
    FakeSTS().start()
}
