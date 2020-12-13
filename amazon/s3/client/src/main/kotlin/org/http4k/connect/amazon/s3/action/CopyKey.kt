package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

data class CopyKey(val sourceBucket: BucketName, val source: BucketKey, val destination: BucketKey): S3BucketAction<Unit> {
    override fun toRequest(region: Region) = Request(PUT, uri()
    ).header("x-amz-copy-source", "$sourceBucket/${source}")

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(Unit)
            else -> Failure(RemoteFailure(PUT, uri(), status))
        }
    }

    private fun uri() = Uri.of("/${destination}")
}
