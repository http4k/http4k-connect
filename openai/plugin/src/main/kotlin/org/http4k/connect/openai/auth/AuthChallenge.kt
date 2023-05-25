package org.http4k.connect.openai.auth

import org.http4k.core.HttpHandler
import org.http4k.core.Request

interface AuthChallenge<T>{
    val challenge: HttpHandler
    operator fun invoke(request: Request): T?
}
