package org.http4k.connect.amazon.s3

import org.http4k.connect.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes

class FakeS3(private val buckets: Storage<Map<String, ByteArray>> = Storage.InMemory()) : ChaosFake() {
    override val app = routes(
        "/" bind GET to {
            Response(OK)
        }
    )
}

fun main() {
    FakeS3().start()
}
