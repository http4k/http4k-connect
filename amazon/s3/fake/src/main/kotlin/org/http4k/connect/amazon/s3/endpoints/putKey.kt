package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.s3.BucketKeyContent
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.storage.Storage
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import java.time.Clock
import java.time.ZonedDateTime
import java.util.Base64

fun bucketPutKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>, clock: Clock) =
    "/{bucketKey:.+}" bind Method.PUT to {
        putKey(it.subdomain(buckets), it.path("bucketKey")!!, it.body.payload.array(), buckets, bucketContent, clock)
    }

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
        Response(Status.CREATED)
    }
    ?: invalidBucketNameResponse()
