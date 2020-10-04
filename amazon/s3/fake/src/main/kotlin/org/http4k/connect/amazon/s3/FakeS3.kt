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
import org.http4k.template.ViewModel
import org.http4k.template.viewModel

data class ListAllMyBuckets(val buckets: List<BucketName>) : ViewModel

class FakeS3(private val buckets: Storage<Unit> = Storage.InMemory()) : ChaosFake() {
    private val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()

    override val app = routes(
        "/{id:.+}" bind routes(
            PUT to {
                val key = it.path("id")!!
                buckets[key] ?: { buckets[key] = Unit }()
                Response(CREATED)
            },
            DELETE to {
                val key = it.path("id")!!
                Response(if (buckets.remove(key)) OK else NOT_FOUND)
            }
        ),
        "/" bind GET to {
            Response(OK).with(lens of ListAllMyBuckets(buckets.keySet("", ::BucketName).toList()))
        }
    )
}

fun main() {
    FakeS3().start()
}
