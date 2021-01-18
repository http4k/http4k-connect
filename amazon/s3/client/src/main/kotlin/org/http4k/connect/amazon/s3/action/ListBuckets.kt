package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Listing
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.text
import org.http4k.connect.amazon.model.xmlDoc
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

@Http4kConnectAction
class ListBuckets : S3Action<Listing<BucketName>> {

    override fun toRequest() = Request(Method.GET, Uri.of("/"))

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> {
                val buckets = xmlDoc().getElementsByTagName("Name")
                val items = (0 until buckets.length).map { BucketName.of(buckets.item(it).text()) }
                Success(if (items.isNotEmpty()) Listing.Unpaged(items) else Listing.Empty)
            }
            else -> Failure(RemoteFailure(Method.GET, Uri.of("/"), status))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
