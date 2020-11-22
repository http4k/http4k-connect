package org.http4k.connect.storage

import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    Storage.InMemory<String>().asHttpHandler().asServer(SunHttp(8080)).start()
}
