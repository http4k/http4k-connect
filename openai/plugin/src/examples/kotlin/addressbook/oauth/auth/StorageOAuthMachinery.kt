package addressbook.oauth.auth

import org.http4k.connect.openai.auth.oauth.OAuthMachinery
import org.http4k.connect.openai.auth.oauth.SecureStrings
import org.http4k.connect.openai.auth.oauth.UserChallenge
import java.time.Clock
import java.time.Duration

/**
 * Construction of OAuthMachinery backed by storage providers.
 */
fun <Principal : Any> StorageOAuthMachinery(
    storageProvider: StorageProvider,
    strings: SecureStrings,
    validity: Duration,
    tokenLifespan: Duration,
    cookieDomain: String,
    clock: Clock,
    userChallenge: UserChallenge<Principal>
): OAuthMachinery<Principal> {
    val principalStore = StoragePrincipalStore<Principal>(storageProvider(), storageProvider(), clock)
    val accessTokens = StorageAccessTokens(principalStore, strings, tokenLifespan)

    return OAuthMachinery(
        userChallenge,
        principalStore,
        accessTokens,
        StorageRefreshTokens(storageProvider(), accessTokens),
        StorageAuthorizationCodes(storageProvider(), clock, strings, validity),
        StorageAuthRequestTracking(storageProvider(), cookieDomain, clock, strings, validity)
    )
}
