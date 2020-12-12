package org.http4k.connect.example

import org.http4k.core.HttpHandler

fun Example.Companion.Http(httpHandler: HttpHandler) = object : Example {
    override fun <R> invoke(request: ExampleAction<R>) = request.toResult(httpHandler(request.toRequest()))
}
