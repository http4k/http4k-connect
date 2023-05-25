package org.http4k.connect.openai.auth.oauth.impl

import org.http4k.connect.openai.auth.oauth.AccessTokenStore
import org.http4k.connect.openai.auth.oauth.AuthCodeStore
import org.http4k.connect.storage.Storage
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AuthorizationCode

fun <T : Any> StorageAccessTokenStore(storage: Storage<T>) = object : AccessTokenStore<T> {
    override fun get(key: AccessToken) = storage[key.value]

    override fun minusAssign(code: AccessToken) {
        storage.remove(code.value)
    }

    override fun set(key: AccessToken, data: T) {
        storage[key.value] = data
    }
}
fun <T : Any> StorageAuthCodeStore(storage: Storage<T>) = object : AuthCodeStore<T> {
    override fun get(key: AuthorizationCode) = storage[key.value]

    override fun minusAssign(data: AuthorizationCode) {
        storage.remove(data.value)
    }

    override fun set(key: AuthorizationCode, data: T) {
        storage[key.value] = data
    }
}
