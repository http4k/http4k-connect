package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import java.io.InputStream

data class PutKey(val key: BucketKey, val content: InputStream) : S3BucketAction<Unit> {
    override fun toRequest(region: Region) = Request(PUT, uri()).body(content)

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful || status.redirection -> Success(Unit)
            else -> Failure(RemoteFailure(PUT, uri(), status))
        }
    }

    private fun uri() = Uri.of("/${key}")
}
