package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.s3.S3BucketAction
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.asRemoteFailure
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

@Http4kConnectAction
data class CopyObject(val sourceBucket: BucketName, val source: BucketKey, val destination: BucketKey) :
    S3BucketAction<Unit> {
    override fun toRequest() = Request(PUT, uri())
        .header("x-amz-copy-source", "$sourceBucket/${source}")

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(Unit)
            else -> Failure(asRemoteFailure(this))
        }
    }

    private fun uri() = Uri.of("/${destination}")
}
