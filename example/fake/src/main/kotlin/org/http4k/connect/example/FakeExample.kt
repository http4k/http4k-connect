package org.http4k.connect.example

import org.http4k.connect.common.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.StorageProvider
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import java.util.UUID

val DEFAULT_PORT = 30099

class FakeExample(storageProvider: StorageProvider<String> = StorageProvider.InMemory()) : ChaosFake() {
    private val echos = storageProvider("calls")
    override val app = { req: Request ->
        echos.create(UUID.randomUUID().toString(), req.bodyString())
        Response(OK).body(req.body)
    }
}

fun main() {
    FakeExample().asServer(SunHttp(DEFAULT_PORT)).start()
}
