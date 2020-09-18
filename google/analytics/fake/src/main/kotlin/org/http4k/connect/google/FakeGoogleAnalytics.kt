package org.http4k.connect.google

import org.http4k.connect.common.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.StorageProvider
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import java.util.UUID

val DEFAULT_PORT = 30000

class FakeGoogleAnalytics(storageProvider: StorageProvider<Uri> = StorageProvider.InMemory()) : ChaosFake() {
    private val views = storageProvider("views")

    override val app = routes(
        "/collect" bind POST to {
            views.create(UUID.randomUUID().toString(), it.uri)
            Response(OK).body(it.body) }
    )
}

fun main() {
    FakeGoogleAnalytics().asServer(SunHttp(DEFAULT_PORT)).start()
}
