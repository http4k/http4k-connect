package org.http4k.connect.openai.auth.oauth

import org.http4k.security.oauth.server.AuthorizationCode

interface AuthCodeStore<T : Any> {
    operator fun set(key: AuthorizationCode, data: T)
    operator fun minusAssign(data: AuthorizationCode)
}
