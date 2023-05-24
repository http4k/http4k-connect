package org.http4k.connect.openai.auth.oauth

import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AccessTokens
import org.http4k.security.oauth.server.AuthRequestTracking
import org.http4k.security.oauth.server.AuthorizationCodes
import org.http4k.security.oauth.server.refreshtoken.RefreshTokens
import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * Simple implementation of OAuthMachinery, backed by storage providers.
 */
fun StorageOAuthMachinery(
    storageProvider: StorageProvider,
    strings: SecureStrings,
    validity: Duration,
    cookieDomain: String,
    clock: Clock
): OAuthMachinery {
    val tokenStorage = storageProvider<Instant>()
    val accessTokens = StorageAccessTokens(tokenStorage, strings, validity, clock)
    return object : OAuthMachinery,
        AccessTokens by accessTokens,
        AuthorizationCodes by StorageAuthorizationCodes(storageProvider(), clock, strings, validity),
        AuthRequestTracking by StorageAuthRequestTracking(storageProvider(), cookieDomain, clock, strings, validity),
        RefreshTokens by StorageRefreshTokens(storageProvider(), accessTokens) {
        override fun validate(accessToken: AccessToken) = tokenStorage[accessToken.value]
            ?.let {
                clock.instant().isBefore(it)
                    .also { if (!it) tokenStorage.remove(accessToken.value) }
            }
            ?: false
    }
}
