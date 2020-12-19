package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketName
import org.http4k.core.Method.DELETE
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Uri

@Http4kConnectAction
data class DeleteBucket(val bucketName: BucketName) : S3Action<Unit?> {

    override fun toRequest() = Request(DELETE, uri())

    private fun uri() = Uri.of(bucketName.value)

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(Unit)
            status == NOT_FOUND -> Success(null)
            else -> Failure(RemoteFailure(DELETE, uri(), status))
        }
    }
}
