package org.http4k.connect.example

import org.http4k.connect.example.action.ExampleAction
import org.http4k.core.HttpHandler

fun Example.Companion.Http(httpHandler: HttpHandler) = object : Example {
    override operator fun <R : Any> invoke(request: ExampleAction<R>) = request.toResult(httpHandler(request.toRequest()))
}
