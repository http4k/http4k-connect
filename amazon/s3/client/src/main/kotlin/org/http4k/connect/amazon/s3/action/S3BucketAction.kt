package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Request
import org.http4k.core.Response

interface S3BucketAction<R> {
    fun toRequest(region: Region): Request
    fun toResult(response: Response): Result<R, RemoteFailure>
}
