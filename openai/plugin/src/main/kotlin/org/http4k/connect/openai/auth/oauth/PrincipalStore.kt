package org.http4k.connect.openai.auth.oauth

import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AuthorizationCode
import java.time.Instant

/**
 * Provides storage of the Principal
 */
interface PrincipalStore<Principal : Any> {
    operator fun get(key: AccessToken): Principal?
    operator fun set(key: AccessToken, data: Pair<Principal, Instant>)

    operator fun get(key: AuthorizationCode): Principal?
    operator fun set(key: AuthorizationCode, data: Principal)
}
