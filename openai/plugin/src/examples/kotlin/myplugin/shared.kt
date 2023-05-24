package myplugin

import org.http4k.cloudnative.env.Environment
import org.http4k.connect.openai.auth.oauth.StorageProvider
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Credentials
import org.http4k.lens.BiDiLensSpec

fun BiDiLensSpec<Environment, String>.credentials() = map({
    it.split(":")
        .let { (clientId, clientSecret) -> Credentials(clientId, clientSecret) }
}, {
    it.user + ":" + it.password
})

object InMemoryStorageProvider : StorageProvider {
    override fun <T : Any> invoke() = Storage.InMemory<T>()
}
