package org.http4k.connect.openai.auth.oauth.impl

import org.http4k.connect.openai.auth.AuthChallenge
import org.http4k.connect.openai.auth.oauth.OAuthMachinery
import org.http4k.connect.openai.auth.oauth.SecureStrings
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AccessTokens
import org.http4k.security.oauth.server.AuthRequestTracking
import org.http4k.security.oauth.server.AuthorizationCode
import org.http4k.security.oauth.server.AuthorizationCodes
import org.http4k.security.oauth.server.refreshtoken.RefreshTokens
import java.security.Principal
import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * Simple implementation of OAuthMachinery, backed by storage providers.
 */
fun <Principal : Any> StorageOAuthMachinery(
    storageProvider: StorageProvider,
    strings: SecureStrings,
    validity: Duration,
    cookieDomain: String,
    clock: Clock,
    authChallenge: AuthChallenge<Principal>
): OAuthMachinery<Principal> {
    val tokenStorage = storageProvider<Pair<Principal, Instant>>()
    val codeStorage = storageProvider<Principal>()

    val accessTokens = StorageAccessTokens(tokenStorage, codeStorage, strings, validity, clock)

    return object : OAuthMachinery<Principal>,
        AccessTokens by accessTokens,
        AuthorizationCodes by StorageAuthorizationCodes(storageProvider(), codeStorage, clock, strings, validity),
        AuthRequestTracking by StorageAuthRequestTracking(storageProvider(), cookieDomain, clock, strings, validity),
        RefreshTokens by StorageRefreshTokens(storageProvider(), accessTokens),
        AuthChallenge<Principal> by authChallenge {

        override fun get(key: AccessToken) = tokenStorage[key.value]?.first

        override operator fun set(key: AuthorizationCode, data: Principal) {
            codeStorage[key.value] = data
        }
    }
}
