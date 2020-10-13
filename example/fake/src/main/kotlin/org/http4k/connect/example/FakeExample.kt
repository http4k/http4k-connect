package org.http4k.connect.example

import org.http4k.connect.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import java.util.UUID

class FakeExample(private val echos: Storage<String> = Storage.InMemory()) : ChaosFake() {
    override val app = { req: Request ->
        echos[UUID.randomUUID().toString()] = req.bodyString()
        Response(OK).body(req.body)
    }
}

fun main() {
    FakeExample().start()
}
