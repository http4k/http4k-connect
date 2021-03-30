package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Paged
import org.http4k.connect.PagedAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.model.firstChild
import org.http4k.connect.amazon.model.firstChildText
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
    PagedAction<String, ObjectSummary, ObjectList, ListObjectsV2> {
    override fun toRequest() = Request(GET, uri()).query("list-type", "2")
        .let { rq -> continuationToken?.let { rq.query("continuation-token", it) } ?: rq }

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> {
                val xmlDoc = xmlDoc()
                val contents = xmlDoc.getElementsByTagName("Contents")
                Success(
                    ObjectList(
                        (0 until contents.length)
                            .map { contents.item(it) }
                            .map {
                                ObjectSummary(
                                    it.firstChildText("ETag"),
                                    BucketKey.of(it.firstChildText("Key")!!),
                                    it.firstChildText("Key")?.toLong()?.let { Timestamp.of(it) },
                                    it.firstChildText("DisplayName"),
                                    it.firstChildText("ID"),
                                    it.firstChild("Owner")?.let {
                                        Owner(it.firstChildText("DisplayName"), it.firstChildText("ID"))
                                    },
                                    it.firstChildText("Size")?.toInt(),
                                    it.firstChildText("StorageClass")?.let { StorageClass.valueOf(it) }
                                )
                            },
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

data class Owner(val DisplayName: String?, val ID: String?)

enum class StorageClass {
    STANDARD, REDUCED_REDUNDANCY, GLACIER, STANDARD_IA, ONEZONE_IA, INTELLIGENT_TIERING, DEEP_ARCHIVE, OUTPOSTS
}

data class ObjectSummary(
    val ETag: String?,
    val Key: BucketKey,
    val LastModified: Timestamp?,
    val DisplayName: String?,
    val ID: String?,
    val Owner: Owner?,
    val Size: Int?,
    val StorageClass: StorageClass?
)

data class ObjectList(
    override val items: List<ObjectSummary>,
    val continuationToken: String? = null
) : Paged<String, ObjectSummary> {
    override fun token() = continuationToken
}
