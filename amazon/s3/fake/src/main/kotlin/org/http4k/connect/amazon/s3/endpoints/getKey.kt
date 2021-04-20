package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.s3.BucketKeyContent
import org.http4k.connect.amazon.s3.S3Error
import org.http4k.connect.storage.Storage
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.path
import java.util.Base64

fun pathBasedBucketGetKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
    "/{bucketName}/{bucketKey:.+}" bind Method.GET to { req ->
        bucketGetKey(buckets, req.path("bucketName")!!, bucketContent, req)
    }

fun bucketGetKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
    "/{bucketKey:.+}" bind Method.GET to { req ->
        bucketGetKey(buckets, req.subdomain(buckets), bucketContent, req)
    }

fun bucketGetKey(
    buckets: Storage<Unit>,
    bucket: String,
    bucketContent: Storage<BucketKeyContent>,
    req: Request
) = (buckets[bucket]
    ?.let {
        bucketContent["${bucket}-${req.path("bucketKey")!!}"]
            ?.content?.let { Base64.getDecoder().decode(it).inputStream() }
            ?.let { Response(Status.OK).body(it) }
            ?: Response(Status.NOT_FOUND).with(lens of S3Error("NoSuchKey"))
    }
    ?: invalidBucketNameResponse())
