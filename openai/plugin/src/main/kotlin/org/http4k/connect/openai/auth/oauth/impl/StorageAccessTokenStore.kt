package org.http4k.connect.openai.auth.oauth.impl

import org.http4k.connect.openai.auth.oauth.AccessTokenStore
import org.http4k.connect.storage.Storage
import org.http4k.security.AccessToken

fun <Principal : Any> StorageAccessTokenStore(storage: Storage<Principal>) = object : AccessTokenStore<Principal> {
    override fun get(key: AccessToken) = storage[key.value]

    override fun minusAssign(data: AccessToken) {
        storage.remove(data.value)
    }

    override fun set(key: AccessToken, data: Principal) {
        storage[key.value] = data
    }
}
