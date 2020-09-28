package org.http4k.connect

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class CapturingHttpHandler : HttpHandler {
    lateinit var captured: Request
    var response: Response = Response(Status.OK)

    override fun invoke(request: Request): Response {
        captured = request
        return response
    }
}
