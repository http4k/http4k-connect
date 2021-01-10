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

    private fun isS3(): Router = { it: Request -> it.subdomain(buckets) == "s3" }.asRouter()
    private fun isBucket(): Router = { it: Request -> it.subdomain(buckets) != "s3" }.asRouter()

    override val app = routes(
        isS3() bind routes(
            globalListBucketKeys(buckets, bucketContent),
            globalPutBucket(buckets),
            globalListBuckets(buckets)
        ),
        isBucket() bind routes(
            bucketGetKey(buckets, bucketContent),
            copyKey(bucketContent, buckets),
            bucketPutKey(buckets, bucketContent),
            bucketDeleteKey(buckets, bucketContent),
            bucketPutBucket(buckets),
            bucketDeleteBucket(buckets),
            bucketListKeys()
        )
    )

    private fun bucketListKeys() = "/" bind GET to { listBucketKeys(it.subdomain(buckets), buckets, bucketContent) }

    private fun bucketDeleteBucket(buckets: Storage<Unit>) =
        "/" bind DELETE to { Response(if (buckets.remove(it.subdomain(buckets))) OK else NOT_FOUND) }

    private fun bucketPutBucket(buckets: Storage<Unit>) = "/" bind PUT to { putBucket(it.subdomain(buckets), buckets) }

    private fun bucketDeleteKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
        "/{id:.+}" bind DELETE to { req ->
            val bucket = req.subdomain(buckets)
            (buckets[bucket]
                ?.let { Response(if (bucketContent.remove("${bucket}-${req.path("id")!!}")) OK else NOT_FOUND) }
                ?: Response(NOT_FOUND))
        }

    private fun bucketPutKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
        "/{id:.+}" bind PUT to {
            putKey(it.subdomain(buckets), it.path("id")!!, it.body.payload.array(), buckets, bucketContent, clock)
        }

    private fun copyKey(
        bucketContent: Storage<BucketKeyContent>, buckets: Storage<Unit>
    ) = "/{id:.+}" bind PUT to routes(headers("x-amz-copy-source") bind { req ->
        bucketContent[req.header("x-amz-copy-source")!!.split("/")
            .let { (sourceBucket, sourceKey) -> "$sourceBucket-$sourceKey" }]
            ?.let {
                putKey(
                    req.subdomain(buckets),
                    req.path("id")!!,
                    Base64.getDecoder().decode(it.content),
                    buckets,
                    bucketContent,
                    clock
                )
                Response(OK)
            } ?: Response(NOT_FOUND)
    }
    )

    private fun bucketGetKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
        "/{id:.+}" bind GET to { req ->
            val bucket = req.subdomain(buckets)
            buckets[bucket]
                ?.let {
                    bucketContent["${bucket}-${req.path("id")!!}"]?.content?.let {
                        Base64.getDecoder().decode(it).inputStream()
                    }
                }?.let { Response(OK).body(it) }
                ?: Response(NOT_FOUND)
        }

    private fun globalListBuckets(buckets: Storage<Unit>) = "/" bind GET to {
        Response(OK)
            .with(lens of ListAllMyBuckets(buckets.keySet("").map { BucketName.of(it) }.toList().sortedBy { it.value }))
    }

    private fun globalPutBucket(buckets: Storage<Unit>) = "/{id:.+}" bind PUT to { putBucket(it.path("id")!!, buckets) }

    private fun globalListBucketKeys(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
        "/{id:.+}" bind GET to {
            listBucketKeys(
                "s3",
                buckets, bucketContent
            )
        }

    private fun listBucketKeys(bucket: String, buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
        buckets[bucket]
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

    private fun putKey(
        bucket: String,
        key: String,
        bytes: ByteArray,
        buckets: Storage<Unit>,
        bucketContent: Storage<BucketKeyContent>,
        clock: Clock
    ) = buckets[bucket]
        ?.let {
            bucketContent["$bucket-$key"] = BucketKeyContent(
                BucketKey.of(key),
                Base64.getEncoder().encodeToString(bytes),
                ZonedDateTime.now(clock)
            )
            Response(CREATED)
        }
        ?: Response(NOT_FOUND)

    private fun putBucket(id: String, buckets: Storage<Unit>): Response {
        buckets[id] ?: run { buckets[id] = Unit }
        return Response(CREATED)
    }

    private fun Request.subdomain(buckets: Storage<Unit>): String =
        (header("x-forwarded-host") ?: header("host"))?.split('.')?.firstOrNull() ?: run {
            buckets[GLOBAL_BUCKET] = Unit
            GLOBAL_BUCKET
        }

    /**
     * Convenience function to get an S3 client for global operations
     */
    fun s3Client() = S3.Http({ AwsCredentials("accessKey", "secret") }, this, clock)

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
