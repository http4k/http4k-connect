package addressbook.oauth.auth

import org.http4k.connect.openai.auth.oauth.PrincipalStore
import org.http4k.connect.storage.Storage
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AuthorizationCode
import java.time.Instant

class StoragePrincipalStore<Principal: Any>(
    private val tokenStorage: Storage<Pair<Principal, Instant>>,
    private val codeStorage: Storage<Principal>
) : PrincipalStore<Principal> {

    override fun get(key: AccessToken) = tokenStorage[key.value]?.first

    override fun set(key: AccessToken, data: Pair<Principal, Instant>) {
        tokenStorage[key.value] = data
    }

    override fun get(key: AuthorizationCode) = codeStorage[key.value].also {
        codeStorage.remove(key.value)
    }

    override operator fun set(key: AuthorizationCode, data: Principal) {
        codeStorage[key.value] = data
    }
}
