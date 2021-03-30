package org.http4k.connect.amazon.s3

import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
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
import org.http4k.routing.bind
import org.http4k.routing.headers
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.time.Clock
import java.time.ZonedDateTime
import java.util.Base64


fun bucketListObjectsV2(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
    "/" bind GET to { listObjectsV2(it.subdomain(buckets), buckets, bucketContent) }

fun bucketDeleteBucket(buckets: Storage<Unit>) =
    "/" bind DELETE to {
        if (buckets.remove(it.subdomain(buckets))) Response(OK)
        else invalidBucketNameResponse()
    }

fun bucketPutBucket(buckets: Storage<Unit>) = "/" bind PUT to { putBucket(it.subdomain(buckets), buckets) }

fun bucketDeleteKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
    "/{id:.+}" bind DELETE to { req ->
        val bucket = req.subdomain(buckets)
        buckets[bucket]
            ?.let {
                if (bucketContent.remove("${bucket}-${req.path("id")!!}")) Response(OK)
                else Response(NOT_FOUND).with(lens of S3Error("NoSuchKey"))
            }
            ?: invalidBucketNameResponse()
    }

fun bucketPutKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>, clock: Clock) =
    "/{id:.+}" bind PUT to {
        putKey(it.subdomain(buckets), it.path("id")!!, it.body.payload.array(), buckets, bucketContent, clock)
    }

fun copyKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>, clock: Clock) =
    "/{id:.+}" bind PUT to routes(headers("x-amz-copy-source") bind { req ->
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
            } ?: invalidBucketNameResponse()
    })

fun bucketGetKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
    "/{id:.+}" bind GET to { req ->
        val bucket = req.subdomain(buckets)
        buckets[bucket]
            ?.let {
                bucketContent["${bucket}-${req.path("id")!!}"]
                    ?.content?.let { Base64.getDecoder().decode(it).inputStream() }
                    ?.let { Response(OK).body(it) }
                    ?: Response(NOT_FOUND).with(lens of S3Error("NoSuchKey"))
            }
            ?: invalidBucketNameResponse()
    }

fun globalListBuckets(buckets: Storage<Unit>) = "/" bind GET to {
    Response(OK)
        .with(lens of ListAllMyBuckets(buckets.keySet("").map { BucketName.of(it) }.toList().sortedBy { it.value }))
}

fun globalPutBucket(buckets: Storage<Unit>) = "/{id:.+}" bind PUT to { putBucket(it.path("id")!!, buckets) }

fun globalListObjectsV2(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
    "/{id:.+}" bind GET to {
        listObjectsV2(
            "s3",
            buckets, bucketContent
        )
    }

fun listObjectsV2(bucket: String, buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
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

fun putKey(
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
    ?: invalidBucketNameResponse()

fun putBucket(bucket: String, buckets: Storage<Unit>): Response {
    buckets[bucket] ?: run { buckets[bucket] = Unit }
    return Response(CREATED)
}

fun Request.subdomain(buckets: Storage<Unit>): String =
    (header("x-forwarded-host") ?: header("host"))
        ?.split('.')
        ?.firstOrNull()
        ?: run {
            buckets[GLOBAL_BUCKET] = Unit
            GLOBAL_BUCKET
        }

private val lens by lazy {
    Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()
}

private const val GLOBAL_BUCKET = "unknown"

private fun invalidBucketNameResponse() = Response(NOT_FOUND).with(lens of S3Error("NoSuchBucket"))
