package org.http4k.connect.amazon.s3

import org.http4k.connect.ChaosFake
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.METHOD_NOT_ALLOWED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.time.Clock
import java.time.Instant

/**
 * Global S3 operations (manage buckets)
 */
class FakeS3(
    private val buckets: Storage<Storage<BucketKeyContent>> = Storage.InMemory(),
    private val toBucket: (BucketName) -> Storage<BucketKeyContent> = { Storage.InMemory() },
    private val clock: Clock = Clock.systemDefaultZone()) : ChaosFake() {

    private val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()

    override val app = routes(
        "/{id:.+}" bind routes(
            GET to {
                val id = it.path("id")!!
                when (val subdomain = it.subdomain()) {
                    "s3" -> listBucketKeys(subdomain) //??
                    else -> getKey(subdomain, id)
                }
            },
            PUT to {
                val id = it.path("id")!!
                when (val subdomain = it.subdomain()) {
                    "s3" -> putBucket(id)
                    else -> putKey(subdomain, id, it.body.payload.array())
                }
            },
            DELETE to {
                val id = it.path("id")!!
                when (val subdomain = it.subdomain()) {
                    "s3" -> deleteBucket(id)
                    else -> deleteKey(subdomain, id)
                }
            }
        ),
        "/" bind routes(
            PUT to {
                when (val subdomain = it.subdomain()) {
                    "s3" -> Response(METHOD_NOT_ALLOWED)
                    else -> putBucket(subdomain)
                }
            },
            DELETE to {
                when (val subdomain = it.subdomain()) {
                    "s3" -> Response(METHOD_NOT_ALLOWED)
                    else -> deleteBucket(subdomain)
                }
            },
            GET to {
                when (val subdomain = it.subdomain()) {
                    "s3" -> listBuckets()
                    else -> listBucketKeys(subdomain)
                }
            }
        )
    )

    private fun getKey(bucket: String, key: String) = buckets[bucket]
        ?.let {
            it[key]?.content?.inputStream()
        }?.let { Response(OK).body(it) }
         ?: Response(NOT_FOUND)

    private fun listBucketKeys(bucket: String) = buckets[bucket]
        ?.let { contents ->
            Response(OK)
                .with(lens of ListBucketResult(
                    contents.keySet("") { it }
                        .map { contents[it]!! }
                ))
        }
        ?: Response(NOT_FOUND)

    private fun listBuckets() = Response(OK)
        .with(lens of ListAllMyBuckets(buckets.keySet("", ::BucketName).toList()))

    private fun deleteBucket(bucket: String) = Response(if (buckets.remove(bucket)) OK else NOT_FOUND)

    private fun deleteKey(bucket: String, key: String) = (buckets[bucket]
        ?.let { Response(if (it.remove(key)) OK else NOT_FOUND) }
        ?: Response(NOT_FOUND))

    private fun putKey(bucket: String, key: String, bytes: ByteArray) = buckets[bucket]
        ?.let {
            it[key] = BucketKeyContent(BucketKey(key), bytes, Instant.now(clock))
            Response(CREATED)
        }
        ?: Response(NOT_FOUND)

    private fun putBucket(id: String): Response {
        buckets[id] ?: { buckets[id] = toBucket(BucketName(id)) }()
        return Response(CREATED)
    }

    private fun Request.subdomain() = header("host")?.split('.')?.firstOrNull() ?: ""
}

fun main() {
    FakeS3().start()
}
