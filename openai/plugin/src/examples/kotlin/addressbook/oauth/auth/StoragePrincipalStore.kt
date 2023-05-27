package addressbook.oauth.auth

import org.http4k.connect.openai.auth.oauth.PrincipalStore
import org.http4k.connect.storage.Storage
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AuthorizationCode
import java.time.Clock
import java.time.Instant
import kotlin.Long.Companion.MAX_VALUE

class StoragePrincipalStore<Principal : Any>(
    private val tokenStorage: Storage<Pair<Principal, Instant>>,
    private val codeStorage: Storage<Principal>,
    private val clock: Clock
) : PrincipalStore<Principal> {

    override fun get(key: AccessToken) = tokenStorage[key.value]
        ?.takeIf { it.second.isAfter(clock.instant()) }
        ?.first

    override fun set(key: AccessToken, data: Principal) {
        tokenStorage[key.value] = data to clock.instant().plusSeconds(key.expiresIn ?: MAX_VALUE)
    }

    override fun get(key: AuthorizationCode) = codeStorage[key.value].also {
        codeStorage.remove(key.value)
    }

    override operator fun set(key: AuthorizationCode, data: Principal) {
        codeStorage[key.value] = data
    }
}
