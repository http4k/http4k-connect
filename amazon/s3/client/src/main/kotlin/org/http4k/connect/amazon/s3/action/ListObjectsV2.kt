package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Paged
import org.http4k.connect.PagedAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.text
import org.http4k.connect.amazon.model.xmlDoc
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

/**
 * List items in a bucket. Note that the S3 API maxes out at 1000 items.
 */
@Http4kConnectAction
data class ListObjectsV2(val continuationToken: String? = null) : S3BucketAction<ObjectList>,
    PagedAction<String, BucketKey, ObjectList, ListObjectsV2> {
    override fun toRequest() = Request(GET, uri()).query("list-type", "2")
        .let { rq -> continuationToken?.let { rq.query("continuation-token", it) } ?: rq }

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> {
                val xmlDoc = xmlDoc()
                val keys = xmlDoc.getElementsByTagName("Key")
                val items = (0 until keys.length).map { BucketKey.of(keys.item(it).text()) }
                Success(
                    ObjectList(
                        items,
                        xmlDoc.getElementsByTagName("NextContinuationToken").item(0)?.text()
                    )
                )
            }
            else -> Failure(RemoteFailure(GET, uri(), status))
        }
    }

    private fun uri() = Uri.of("/")

    override fun next(token: String) = copy(continuationToken = token)
}

data class ObjectList(
    override val items: List<BucketKey>,
    val continuationToken: String? = null
) : Paged<String, BucketKey> {
    override fun token() = continuationToken
}
