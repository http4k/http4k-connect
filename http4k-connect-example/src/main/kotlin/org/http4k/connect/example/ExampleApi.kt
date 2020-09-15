package org.http4k.connect.example

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request

class ExampleApi(private val http: HttpHandler) {
    fun echo(input: String) = http(Request(Method.GET, "/echo").body("echo")).bodyString()
}