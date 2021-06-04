package org.http4k.connect.google

import org.http4k.connect.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.Form
import org.http4k.core.body.form
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.UUID

class FakeGoogleAnalytics(val calls: Storage<Form> = Storage.InMemory()) : ChaosFake() {

    override val app = routes(
        "/collect" bind POST to {
            calls[UUID.randomUUID().toString()] = it.form()
            Response(OK).body(it.body)
        }
    )
}

fun main() {
    FakeGoogleAnalytics().start()
}
