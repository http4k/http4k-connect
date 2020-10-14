import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.connect.storage.StorageExplorer
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val storage: Storage<Any> = Storage.InMemory()
    StorageExplorer(storage).asServer(SunHttp(8000)).start()
}
