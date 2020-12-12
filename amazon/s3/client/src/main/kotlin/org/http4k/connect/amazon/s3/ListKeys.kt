package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Listing
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.documentBuilderFactory
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

/**
 * List items in a bucket. Note that the S3 API maxes out at 1000 items.
 */
class ListKeys : S3BucketAction<Listing<BucketKey>> {
    override fun toRequest(region: Region) = Request(GET, uri()).query("list-type", "2")

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> {
                val keys = documentBuilderFactory.parse(body.stream).getElementsByTagName("Key")
                val items = (0 until keys.length).map { BucketKey.of(keys.item(it).textContent) }
                Success(if (items.isNotEmpty()) Listing.Unpaged(items) else Listing.Empty)
            }
            else -> Failure(RemoteFailure(GET, uri(), status))
        }
    }

    private fun uri() = Uri.of("/")
}
