package org.http4k.connect.openai.auth.oauth.impl

import org.http4k.connect.openai.auth.oauth.AccessTokenStore
import org.http4k.connect.openai.auth.oauth.AuthCodeStore
import org.http4k.connect.openai.auth.oauth.OAuthMachinery
import org.http4k.connect.openai.auth.oauth.SecureStrings
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AccessTokens
import org.http4k.security.oauth.server.AuthRequestTracking
import org.http4k.security.oauth.server.AuthorizationCode
import org.http4k.security.oauth.server.AuthorizationCodes
import org.http4k.security.oauth.server.refreshtoken.RefreshTokens
import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * Simple implementation of OAuthMachinery, backed by storage providers.
 */
fun <T: Any> StorageOAuthMachinery(
    storageProvider: StorageProvider,
    strings: SecureStrings,
    validity: Duration,
    cookieDomain: String,
    clock: Clock
): OAuthMachinery<T> {
    val tokenStorage = storageProvider<Instant>()
    val accessTokens = StorageAccessTokens(tokenStorage, strings, validity, clock)
    return object : OAuthMachinery<T>,
        AccessTokens by accessTokens,
        AuthorizationCodes by StorageAuthorizationCodes(storageProvider(), clock, strings, validity),
        AuthRequestTracking by StorageAuthRequestTracking(storageProvider(), cookieDomain, clock, strings, validity),
        RefreshTokens by StorageRefreshTokens(storageProvider(), accessTokens) ,
        AccessTokenStore<T> by object : AccessTokenStore<T> {
            override fun get(key: AccessToken): T? {
                TODO("Not yet implemented")
            }

            override fun minusAssign(code: AccessToken) {
                TODO("Not yet implemented")
            }

            override fun set(key: AccessToken, data: T) {
                TODO("Not yet implemented")
            }

        },
        AuthCodeStore<T> by object : AuthCodeStore<T> {
            override fun get(key: AuthorizationCode): T? {
                TODO("Not yet implemented")
            }

            override fun minusAssign(data: AuthorizationCode) {
                TODO("Not yet implemented")
            }

            override fun set(key: AuthorizationCode, data: T) {
                TODO("Not yet implemented")
            }

        }
    {
        override fun validate(accessToken: AccessToken) = tokenStorage[accessToken.value]
            ?.let {
                clock.instant().isBefore(it)
                    .also { if (!it) tokenStorage.remove(accessToken.value) }
            }
            ?: false
    }
}
