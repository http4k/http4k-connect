package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.s3.BucketKeyContent
import org.http4k.connect.amazon.s3.S3Error
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.DELETE
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.path

fun bucketDeleteKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
    "/{bucketKey:.+}" bind DELETE to { req ->
        deleteKey(req.subdomain(buckets), buckets, bucketContent, req)
    }

fun pathBasedBucketDeleteKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
    "/{bucketName}/{bucketKey:.+}" bind DELETE to { req ->
        deleteKey(req.path("bucketName")!!, buckets, bucketContent, req)
    }

private fun deleteKey(
    bucket: String,
    buckets: Storage<Unit>,
    bucketContent: Storage<BucketKeyContent>,
    req: Request
) = (buckets[bucket]
    ?.let {
        if (bucketContent.remove("${bucket}-${req.path("bucketKey")!!}")) Response(Status.OK)
        else Response(NOT_FOUND).with(lens of S3Error("NoSuchKey"))
    }
    ?: invalidBucketNameResponse())
