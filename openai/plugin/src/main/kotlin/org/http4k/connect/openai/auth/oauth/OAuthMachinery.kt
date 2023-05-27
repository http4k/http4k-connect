package org.http4k.connect.openai.auth.oauth

import org.http4k.security.oauth.server.AccessTokens
import org.http4k.security.oauth.server.AuthRequestTracking
import org.http4k.security.oauth.server.AuthorizationCodes
import org.http4k.security.oauth.server.refreshtoken.RefreshTokens

/**
 * An arrangement of all the pieces of storage and generation various entities of the OAuth flow
 * and to track the in-flight requests.
 */
class OAuthMachinery<Principal : Any>(
    authenticate: UserChallenge<Principal>,
    principalStore: PrincipalStore<Principal>,
    accessTokens: AccessTokens,
    refreshTokens: RefreshTokens,
    authorizationCodes: AuthorizationCodes,
    authRequestTracking: AuthRequestTracking
) :
    UserChallenge<Principal> by authenticate,
    PrincipalStore<Principal> by principalStore,
    AccessTokens by accessTokens,
    RefreshTokens by refreshTokens,
    AuthorizationCodes by authorizationCodes,
    AuthRequestTracking by authRequestTracking
