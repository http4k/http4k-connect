package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.s3.BucketKeyContent
import org.http4k.connect.amazon.s3.TestingHeaders.X_HTTP4K_LAST_MODIFIED
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.storage.Storage
import org.http4k.core.Headers
import org.http4k.core.Method.PUT
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.lens.LastModified
import org.http4k.routing.bind
import org.http4k.routing.path
import java.time.Clock
import java.time.Instant
import java.util.Base64

fun bucketPutKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>, clock: Clock) =
    "/{bucketKey:.+}" bind PUT to {
        putKey(
            it.subdomain(buckets),
            it.path("bucketKey")!!,
            it.body.payload.array(),
            buckets,
            bucketContent,
            clock,
            it.headers
        )
    }

fun pathBasedBucketPutKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>, clock: Clock) =
    "/{bucketName}/{bucketKey:.+}" bind PUT to {
        putKey(
            it.path("bucketName")!!,
            it.path("bucketKey")!!,
            it.body.payload.array(),
            buckets,
            bucketContent,
            clock,
            it.headers
        )
    }

fun putKey(
    bucket: String,
    key: String,
    bytes: ByteArray,
    buckets: Storage<Unit>,
    bucketContent: Storage<BucketKeyContent>,
    clock: Clock,
    headers: Headers
) = buckets[bucket]
    ?.let {
        bucketContent["$bucket-$key"] = BucketKeyContent(
            BucketKey.of(key),
            Base64.getEncoder().encodeToString(bytes),
            lastModified(headers, clock),
            headers.filter {
                it.first !in setOf(
                    "authorization",
                    "x-forwarded-host",
                    "x-amz-content-sha256",
                    "x-amz-date"
                )
            }
        )
        Response(CREATED)
    }
    ?: invalidBucketNameResponse()

private fun lastModified(headers: Headers, clock: Clock) = headers
    .firstOrNull { it.first == X_HTTP4K_LAST_MODIFIED }?.second?.let { LastModified.parse(it) }?.value?.toInstant()
    ?: Instant.now(clock)
