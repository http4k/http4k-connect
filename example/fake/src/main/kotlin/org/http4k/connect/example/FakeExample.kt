package org.http4k.connect.example

import org.http4k.connect.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.routing.routes

class FakeExample(private val echoes: Storage<String> = Storage.InMemory()) : ChaosFake() {
    override val app = routes(
        echo(echoes),
        reverse()
    )

    fun client() = Example.Http(app)
}

fun main() {
    FakeExample().start()
}
