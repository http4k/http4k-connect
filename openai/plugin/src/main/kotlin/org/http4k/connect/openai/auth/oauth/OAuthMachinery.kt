package org.http4k.connect.openai.auth.oauth

import org.http4k.connect.openai.auth.AuthChallenge
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AccessTokens
import org.http4k.security.oauth.server.AuthRequestTracking
import org.http4k.security.oauth.server.AuthorizationCode
import org.http4k.security.oauth.server.AuthorizationCodes
import org.http4k.security.oauth.server.refreshtoken.RefreshTokens

/**
 * Implement this interface to provide storage and generation of the AccessTokens, RefreshTokens, AuthorisationCodes
 * and to track the in-flight requests.
 */
interface OAuthMachinery<Principal : Any> :
    AccessTokens,
    RefreshTokens,
    AuthorizationCodes,
    AuthRequestTracking,
    AuthChallenge<Principal> {

    operator fun get(key: AccessToken): Principal?
    operator fun set(key: AuthorizationCode, data: Principal)

    companion object
}
