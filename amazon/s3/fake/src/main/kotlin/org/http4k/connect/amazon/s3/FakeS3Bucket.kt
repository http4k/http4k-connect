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
import java.time.Clock
import java.time.Instant

data class BucketKeyContent(val key: BucketKey, val content: ByteArray, val modified: Instant) {
    val size = content.size
}

data class ListBucketResult(val keys: List<BucketKeyContent>) : ViewModel {
    val keyCount = keys.size
    val maxKeys = Integer.MAX_VALUE
}

/**
 * Bucket-level S3 operations (get/set/delete content)
 */
class FakeS3Bucket(
    private val bucketContents: Storage<BucketKeyContent> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {
    private val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()

    override val app = routes(
        "/{key:.+}" bind routes(
            GET to {
                val key = it.path("key")!!
                bucketContents[key]?.let {
                    Response(OK).body(Body(it.content.inputStream()))
                } ?: Response(NOT_FOUND)
            },
            PUT to {
                val key = it.path("key")!!
                bucketContents[key] = BucketKeyContent(BucketKey(key), it.body.payload.array(), Instant.now(clock))
                Response(CREATED)
            },
            DELETE to {
                val key = it.path("key")!!
                Response(if (bucketContents.remove(key)) OK else NOT_FOUND)
            }
        ),
        "/" bind GET to {
            Response(OK).with(lens of ListBucketResult(
                bucketContents.keySet("") { it }
                    .map { bucketContents[it]!! }))
        }
    )
}

fun main() {
    FakeS3Bucket().start()
}
