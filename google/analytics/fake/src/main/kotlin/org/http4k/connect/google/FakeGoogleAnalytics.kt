package org.http4k.connect.google

import org.http4k.connect.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.UUID

class FakeGoogleAnalytics(private val views: Storage<Uri> = Storage.InMemory()) : ChaosFake() {

    override val app = routes(
        "/collect" bind POST to {
            views[UUID.randomUUID().toString()] = it.uri
            Response(OK).body(it.body)
        }
    )
}

fun main() {
    FakeGoogleAnalytics().start()
}
