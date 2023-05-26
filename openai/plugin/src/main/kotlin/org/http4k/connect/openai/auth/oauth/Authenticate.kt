package org.http4k.connect.openai.auth.oauth

import org.http4k.core.HttpHandler
import org.http4k.core.Request

interface Authenticate<T>{
    val challenge: HttpHandler
    operator fun invoke(request: Request): T?
}
