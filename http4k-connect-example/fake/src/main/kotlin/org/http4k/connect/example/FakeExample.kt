package org.http4k.connect.example

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class FakeExample() : HttpHandler {
    override fun invoke(p1: Request): Response = Response(Status.OK).body(p1.body)
}