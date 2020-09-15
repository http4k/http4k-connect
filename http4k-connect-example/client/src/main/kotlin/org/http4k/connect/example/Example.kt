package org.http4k.connect.example

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request

interface Example {
    fun echo(input: String): String

    companion object {
        fun Http(httpHandler: HttpHandler) = object : Example {
            override fun echo(input: String) = httpHandler(Request(Method.GET, "echo").body(input)).bodyString()
        }
    }
}

