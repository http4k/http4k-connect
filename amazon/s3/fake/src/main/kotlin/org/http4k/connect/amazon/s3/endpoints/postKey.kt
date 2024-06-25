package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.core.firstChild
import org.http4k.connect.amazon.core.sequenceOfNodes
import org.http4k.connect.amazon.core.text
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.connect.amazon.s3.BucketKeyContent
import org.http4k.connect.amazon.s3.action.RestoreObject
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.RestoreTier
import org.http4k.connect.amazon.s3.replaceHeader
import org.http4k.connect.amazon.s3.requiresRestore
import org.http4k.connect.amazon.s3.storageClass
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

fun bucketPostKey(buckets: Storage<Unit>,  bucketContent: Storage<BucketKeyContent>) = "/{bucketKey:.+}" bind POST to routes(
    queryPresent("restore") bind { request ->
        restoreObject(buckets, bucketContent, request)
    }
)

private fun restoreObject(buckets: Storage<Unit>, bucketContent: Storage<BucketKeyContent>, request: Request): Response {
    val doc = request.xmlDoc().getElementsByTagName("RestoreRequest").sequenceOfNodes().first()

    val data = RestoreObject(
        key = BucketKey.of(request.path("bucketKey")!!),
        days = doc.firstChild("Days")!!.text().toInt(),
        tier = doc.firstChild("GlacierJobParameters")
            ?.firstChild("Tier")
            ?.text()
            ?.let { RestoreTier.valueOf(it) }
    )

    val bucket = request.subdomain(buckets)
    if (buckets[bucket] == null) return invalidBucketNameResponse()
    val obj = bucketContent["$bucket-${data.key}"] ?: return invalidBucketKeyResponse()
    if (!obj.storageClass().requiresRestore()) return invalidObjectStateResponse()

    // instantly restore the object, regardless of restore tier
    // TODO could use clock to approximate restore time
    bucketContent["$bucket-${data.key}"] = obj.replaceHeader("x-amz-restore", "ongoing-request=\"false\"")

    return Response(OK)
}
