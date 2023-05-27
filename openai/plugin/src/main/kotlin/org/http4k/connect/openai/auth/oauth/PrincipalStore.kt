package org.http4k.connect.openai.auth.oauth

import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AuthorizationCode

/**
 * Provides storage of the Principal
 */
interface PrincipalStore<Principal : Any> {
    operator fun get(key: AccessToken): Principal?
    operator fun set(key: AccessToken, data: Principal)

    operator fun get(key: AuthorizationCode): Principal?
    operator fun set(key: AuthorizationCode, data: Principal)
}
