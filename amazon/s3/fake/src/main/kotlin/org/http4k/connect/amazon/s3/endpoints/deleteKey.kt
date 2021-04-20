package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.s3.BucketKeyContent
import org.http4k.connect.amazon.s3.S3Error
import org.http4k.connect.storage.Storage
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.path

fun bucketDeleteKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>) =
    "/{bucketKey:.+}" bind Method.DELETE to { req ->
        val bucket = req.subdomain(buckets)
        buckets[bucket]
            ?.let {
                if (bucketContent.remove("${bucket}-${req.path("bucketKey")!!}")) Response(Status.OK)
                else Response(Status.NOT_FOUND).with(lens of S3Error("NoSuchKey"))
            }
            ?: invalidBucketNameResponse()
    }
