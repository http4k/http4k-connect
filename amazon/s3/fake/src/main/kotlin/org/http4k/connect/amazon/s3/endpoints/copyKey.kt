package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.s3.BucketKeyContent
import org.http4k.connect.storage.Storage
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.headers
import org.http4k.routing.path
import org.http4k.routing.routes
import java.time.Clock
import java.util.Base64

fun copyKey(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>, clock: Clock) =
    "/{bucketKey:.+}" bind Method.PUT to routes(headers("x-amz-copy-source") bind { req ->
        bucketContent[req.header("x-amz-copy-source")!!.split("/")
            .let { (sourceBucket, sourceKey) -> "$sourceBucket-$sourceKey" }]
            ?.let {
                putKey(
                    req.subdomain(buckets),
                    req.path("bucketKey")!!,
                    Base64.getDecoder().decode(it.content),
                    buckets,
                    bucketContent,
                    clock
                )
                Response(Status.OK)
            } ?: invalidBucketNameResponse()
    })
