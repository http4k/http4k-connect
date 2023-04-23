package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.toRemoteFailure
import org.http4k.core.Headers
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import java.io.InputStream

@Http4kConnectAction
data class PutObject(val key: BucketKey, val content: InputStream, val headers: Headers = emptyList()) :
    S3BucketAction<Unit> {
    override fun toRequest() = Request(PUT, Uri.of("/$key")).headers(headers).body(content)

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful || status.redirection -> Success(Unit)
            else -> Failure(toRemoteFailure(this))
        }
    }
}
