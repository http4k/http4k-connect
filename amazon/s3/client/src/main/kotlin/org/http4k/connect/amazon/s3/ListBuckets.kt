package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Listing
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.documentBuilderFactory
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

class ListBuckets : S3Action<Listing<BucketName>> {
    override fun toRequest() = Request(Method.GET, Uri.of("/"))

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> {
                val buckets = documentBuilderFactory.parse(body.stream).getElementsByTagName("Name")
                val items = (0 until buckets.length).map { BucketName.of(buckets.item(it).textContent) }
                Success(if (items.isNotEmpty()) Listing.Unpaged(items) else Listing.Empty)
            }
            else -> Failure(RemoteFailure(Method.GET, Uri.of("/"), status))
        }
    }
}
