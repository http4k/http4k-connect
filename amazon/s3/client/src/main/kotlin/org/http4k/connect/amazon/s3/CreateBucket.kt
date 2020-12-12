package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

data class CreateBucket(val bucketName: BucketName, val region: Region) : S3Action<Unit> {
    private fun uri() = Uri.of("/${bucketName}")

    override fun toRequest() = Request(Method.PUT, uri()).body("""<?xml version="1.0" encoding="UTF-8"?>
<CreateBucketConfiguration xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
    <LocationConstraint>${region}</LocationConstraint>
</CreateBucketConfiguration>""")

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(Unit)
            else -> Failure(RemoteFailure(Method.PUT, uri(), status))
        }
    }
}
