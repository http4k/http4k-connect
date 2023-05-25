package org.http4k.connect.openai.auth.oauth

import org.http4k.security.AccessToken

interface AccessTokenStore<T : Any> {
    operator fun get(key: AccessToken): T?
    operator fun set(key: AccessToken, data: T)
    operator fun minusAssign(data: AccessToken)
}
