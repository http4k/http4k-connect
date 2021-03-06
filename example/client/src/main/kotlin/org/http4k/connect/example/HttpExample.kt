package org.http4k.connect.example

import org.http4k.connect.example.action.ExampleAction
import org.http4k.core.HttpHandler

fun Example.Companion.Http(httpHandler: HttpHandler) = object : Example {
    override fun <R : Any> invoke(action: ExampleAction<R>) = action.toResult(httpHandler(action.toRequest()))
}
