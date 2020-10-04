package org.http4k.connect.amazon.s3

import org.http4k.connect.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

class FakeS3Bucket(private val buckets: Storage<ByteArray> = Storage.InMemory()) : ChaosFake() {
    private val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()

    override val app = routes(
        "/{key:.+}" bind routes(
            GET to {
                val key = it.path("key")!!
                buckets[key]?.let {
                    Response(OK).body(Body(it.inputStream()))
                } ?: Response(NOT_FOUND)
            },
            PUT to {
                val key = it.path("key")!!
                buckets[key] ?: { buckets[key] = it.body.payload.array() }()
                Response(CREATED)
            },
            DELETE to {
                val key = it.path("key")!!
                Response(if (buckets.remove(key)) OK else NOT_FOUND)
            }
        ),
        "/" bind GET to {
            Response(OK).with(lens of ListAllMyBuckets(buckets.keySet("", ::BucketName).toList()))
        }
    )
}

fun main() {
    FakeS3Bucket().start()
}
