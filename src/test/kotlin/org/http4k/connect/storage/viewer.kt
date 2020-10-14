package org.http4k.connect.storage

import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val storageExplorer = StorageExplorer(Storage.InMemory<String>())
    DebuggingFilters.PrintRequestAndResponse().then(storageExplorer)
    .asServer(SunHttp(8080)).start()
}
