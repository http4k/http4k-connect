package org.http4k.connect.google

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class FakeGoogleAnalytics : HttpHandler {
    override fun invoke(request: Request): Response = Response(Status.OK).body(request.body)
}
