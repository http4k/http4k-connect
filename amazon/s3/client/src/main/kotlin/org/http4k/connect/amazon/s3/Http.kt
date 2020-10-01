package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.Payload
import org.xml.sax.InputSource
import java.io.InputStream
import java.io.StringReader
import java.time.Clock
import javax.xml.parsers.DocumentBuilderFactory

fun S3.Companion.Http(http: HttpHandler,
                      scope: AwsCredentialScope,
                      credentialsProvider: () -> AwsCredentials,
                      clock: Clock = Clock.systemDefaultZone(),
                      payloadMode: Payload.Mode = Payload.Mode.Signed
) = S3.Http(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode).then(http))

fun S3.Companion.Http(http: HttpHandler) = object : S3 {
    override fun bucket(bucketName: BucketName): Result<S3.Bucket, RemoteFailure> {
        val status = http(Request(GET, bucketName.url()).query("location", null)).status
        return when {
            status.successful -> Success(S3.Bucket.Http(SetBaseUriFrom(bucketName.url()).then(http)))
            else -> Failure(RemoteFailure(status))
        }
    }

    override fun buckets(): Result<Iterable<BucketName>, RemoteFailure> {

        http(Request(GET, "/"))

        return Success(emptyList())
    }
}

fun S3.Bucket.Companion.Http(http: HttpHandler,
                             bucketName: BucketName,
                             scope: AwsCredentialScope,
                             credentialsProvider: () -> AwsCredentials,
                             clock: Clock = Clock.systemDefaultZone(),
                             payloadMode: Payload.Mode = Payload.Mode.Signed
) = SetBaseUriFrom(bucketName.url())
    .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
    .then(http)

fun S3.Bucket.Companion.Http(http: HttpHandler) = object : S3.Bucket {
    override fun create() = Request(PUT, "").call()
    override fun delete() = Request(DELETE, "").call()
    override fun delete(key: BucketKey) = key.url(DELETE).call()
    override fun set(key: BucketKey, content: InputStream) = key.url(PUT).body(content).call()

    override fun delete(keys: Iterable<BucketKey>): Result<Unit, RemoteFailure> {
        TODO("Not yet implemented")
    }

    override fun get(key: BucketKey) = with(http(key.url(GET))) {
        when {
            status.successful -> Success(body.stream)
            status == NOT_FOUND -> Success(null)
            else -> Failure(RemoteFailure(status))
        }
    }

    override fun list(): Result<BucketKey, RemoteFailure> {
        TODO("Not yet implemented")
    }

    private fun Request.call() = with(http(this)) {
        when {
            status.successful -> Success(Unit)
            else -> Failure(RemoteFailure(status))
        }
    }
}

private fun BucketName.url() = Uri.of("https://$name.s3.amazonaws.com/")

private fun BucketKey.url(method: Method) = Request(method, "/$value")

internal val documentBuilderFactory by lazy {
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
}
