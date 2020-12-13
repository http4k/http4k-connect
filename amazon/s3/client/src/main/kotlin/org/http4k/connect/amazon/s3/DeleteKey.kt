package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Method.DELETE
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri

data class DeleteKey(val key: BucketKey) : S3BucketAction<Unit?> {
    override fun toRequest(region: Region) = Request(DELETE, uri())

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(Unit)
            status == Status.NOT_FOUND -> Success(null)
            else -> Failure(RemoteFailure(DELETE, uri(), status))
        }
    }

    private fun uri() = Uri.of("/${key}")
}
