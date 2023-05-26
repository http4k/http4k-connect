package addressbook.oauth.auth

import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage

object InMemoryStorageProvider : StorageProvider {
    override fun <T : Any> invoke() = Storage.InMemory<T>()
}
