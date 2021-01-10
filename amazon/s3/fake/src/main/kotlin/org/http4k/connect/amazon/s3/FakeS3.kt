package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region
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
import org.http4k.routing.Router
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.routing.headers
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.time.Clock
import java.time.ZonedDateTime
import java.util.Base64

/**
 * Global S3 operations (manage buckets)
 */
class FakeS3(
    private val buckets: Storage<Unit> = Storage.InMemory(),
    private val bucketContent: Storage<BucketKeyContent> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    private val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()

    private val GLOBAL_BUCKET = "unknown"

    private fun isS3(): Router = { it: Request -> it.subdomain() == "s3" }.asRouter()
    private fun isBucket(): Router = { it: Request -> it.subdomain() != "s3" }.asRouter()

    override val app = routes(
        "/{id:.+}" bind GET to routes(isS3() bind { listBucketKeys("s3") }),
        "/{id:.+}" bind GET to { getKey(it.subdomain(), it.path("id")!!) },
        "/{id:.+}" bind PUT to routes(isS3() bind { putBucket(it.path("id")!!) }),
        "/{id:.+}" bind PUT to routes(headers("x-amz-copy-source") bind { copyKey(it.subdomain(), it.header("x-amz-copy-source")!!, it.path("id")!!) }),
        "/{id:.+}" bind PUT to { putKey(it.subdomain(), it.path("id")!!, it.body.payload.array()) },
        "/{id:.+}" bind DELETE to routes(isS3() bind { deleteBucket(it.path("id")!!) }),
        "/{id:.+}" bind DELETE to { deleteKey(it.subdomain(), it.path("id")!!) },
        "/" bind PUT to {
            when (val subdomain = it.subdomain()) {
                "s3" -> Response(METHOD_NOT_ALLOWED)
                else -> putBucket(subdomain)
            }
        },
        "/" bind DELETE to {
            when (val subdomain = it.subdomain()) {
                "s3" -> Response(METHOD_NOT_ALLOWED)
                else -> deleteBucket(subdomain)
            }
        },
        "/" bind GET to {
            when (val subdomain = it.subdomain()) {
                "s3" -> listBuckets()
                else -> listBucketKeys(subdomain)
            }
        }
    )

    private fun copyKey(destinationBucket: String, source: String, destinationKey: String) =
        bucketContent[source.split("/").let { (sourceBucket, sourceKey) -> "$sourceBucket-$sourceKey" }]
            ?.let {
                putKey(destinationBucket, destinationKey, Base64.getDecoder().decode(it.content))
                Response(OK)
            } ?: Response(NOT_FOUND)


    private fun getKey(bucket: String, key: String): Response {
        return buckets[bucket]
            ?.let {
                bucketContent["$bucket-$key"]?.content?.let { Base64.getDecoder().decode(it).inputStream() }
            }?.let { Response(OK).body(it) }
            ?: Response(NOT_FOUND)
    }

    private fun listBucketKeys(bucket: String) = buckets[bucket]
        ?.let {
            Response(OK)
                .with(lens of ListBucketResult(
                    bucketContent.keySet(bucket)
                        .map { it.removePrefix("$bucket-") }
                        .map { bucketContent["$bucket-$it"]!! }
                        .sortedBy { it.key.value }
                ))
        }
        ?: Response(NOT_FOUND)

    private fun listBuckets() = Response(OK)
        .with(lens of ListAllMyBuckets(buckets.keySet("").map { BucketName.of(it) }.toList().sortedBy { it.value }))

    private fun deleteBucket(bucket: String) = Response(if (buckets.remove(bucket)) OK else NOT_FOUND)

    private fun deleteKey(bucket: String, key: String) = (buckets[bucket]
        ?.let { Response(if (bucketContent.remove("$bucket-$key")) OK else NOT_FOUND) }
        ?: Response(NOT_FOUND))

    private fun putKey(bucket: String, key: String, bytes: ByteArray) = buckets[bucket]
        ?.let {
            bucketContent["$bucket-$key"] = BucketKeyContent(
                BucketKey.of(key),
                Base64.getEncoder().encodeToString(bytes),
                ZonedDateTime.now(clock)
            )
            Response(CREATED)
        }
        ?: Response(NOT_FOUND)

    private fun putBucket(id: String): Response {
        buckets[id] ?: run {
            buckets[id] = Unit
        }
        return Response(CREATED)
    }

    private fun Request.subdomain(): String =
        (header("x-forwarded-host") ?: header("host"))?.split('.')?.firstOrNull() ?: run {
            buckets[GLOBAL_BUCKET] = Unit
            GLOBAL_BUCKET
        }

    /**
     * Convenience function to get an S3 client for global operations
     */
    fun s3Client() = S3.Http(
        { AwsCredentials("accessKey", "secret") }, this, clock
    )

    /**
     * Convenience function to get an S3 client for bucket operations
     */
    fun s3BucketClient(name: BucketName, region: Region) = S3Bucket.Http(
        name,
        region,
        { AwsCredentials("accessKey", "secret") }, this, clock
    )
}

fun main() {
    FakeS3().start()
}
