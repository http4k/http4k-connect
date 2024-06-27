package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.core.firstChildText
import org.http4k.connect.amazon.core.model.Tag
import org.http4k.connect.amazon.core.sequenceOfNodes
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.connect.amazon.s3.S3BucketAction
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.asRemoteFailure
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

@Http4kConnectAction
class GetObjectTagging(val key: BucketKey): S3BucketAction<List<Tag>> {

    override fun toRequest() = Request(GET, Uri.of("/$key?tagging"))

    override fun toResult(response: Response): Result<List<Tag>, RemoteFailure> {
        if (!response.status.successful) return Failure(asRemoteFailure(response))

        return response.xmlDoc()
            .getElementsByTagName("Tag")
            .sequenceOfNodes()
            .map { tagEle -> Tag(tagEle.firstChildText("Key")!!, tagEle.firstChildText("Value")!!) }
            .toList()
            .let(::Success)
    }
}
