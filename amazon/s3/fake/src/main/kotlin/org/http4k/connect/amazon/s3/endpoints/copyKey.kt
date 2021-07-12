package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.s3.BucketKeyContent
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.headers
import org.http4k.routing.path
import org.http4k.routing.routes
import java.time.Clock
import java.util.Base64

fun copyKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>, clock: Clock) =
    "/{bucketKey:.+}" bind PUT to routes(headers("x-amz-copy-source") bind { req ->
        copyKey(req.subdomain(buckets), bucketContent, req, buckets, clock)
    })

fun pathBasedCopyKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>, clock: Clock) =
    "/{bucketName}/{bucketKey:.+}" bind PUT to routes(headers("x-amz-copy-source") bind { req ->
        copyKey(req.path("bucketName")!!, bucketContent, req, buckets, clock)
    })

private fun copyKey(
    bucket: String,
    bucketContent: Storage<BucketKeyContent>,
    req: Request,
    buckets: Storage<Unit>,
    clock: Clock
) = bucketContent[req.header("x-amz-copy-source")!!.split("/", limit = 2)
    .let { (sourceBucket, sourceKey) -> "$sourceBucket-$sourceKey" }]
    ?.let {
        putKey(
            bucket,
            req.path("bucketKey")!!,
            Base64.getDecoder().decode(it.content),
            buckets,
            bucketContent,
            clock,
            it.headers
        )
        Response(Status.OK)
    } ?: invalidBucketNameResponse()
