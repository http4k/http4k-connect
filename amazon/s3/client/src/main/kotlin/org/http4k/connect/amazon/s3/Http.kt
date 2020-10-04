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
import org.http4k.filter.Payload
import org.xml.sax.InputSource
import java.io.InputStream
import java.io.StringReader
import java.time.Clock
import javax.xml.parsers.DocumentBuilderFactory

fun S3.Companion.Http(uri: Uri,
                      rawHttp: HttpHandler,
                      scope: AwsCredentialScope,
                      credentialsProvider: () -> AwsCredentials,
                      clock: Clock = Clock.systemDefaultZone(),
                      payloadMode: Payload.Mode = Payload.Mode.Signed) = object : S3 {
    private val http =
        ClientFilters.SetBaseUriFrom(uri)
            .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
            .then(rawHttp)

    override fun buckets(): Result<List<BucketName>, RemoteFailure> {
        val request = Request(GET, "/")
        val response = http(request)

        return with(response) {
            when {
                status.successful -> {
                    val buckets = documentBuilderFactory.parse(body.stream).getElementsByTagName("Name")
                    Success((0 until buckets.length).map { BucketName(buckets.item(it).textContent) })
                }
                else -> Failure(RemoteFailure(request.uri, status))
            }
        }
    }

    override fun create(bucketName: BucketName): Result<Unit, RemoteFailure> {
        val request = Request(PUT, "/$bucketName")
        val response = http(request)

        return with(response) {
            when {
                status.successful -> Success(Unit)
                else -> Failure(RemoteFailure(request.uri, status))
            }
        }
    }

    override fun delete(bucketName: BucketName): Result<Unit?, RemoteFailure> {
        val request = Request(DELETE, "/$bucketName")
        val response = http(request)

        return with(response) {
            when {
                status.successful -> Success(Unit)
                status == NOT_FOUND -> Success(null)
                else -> Failure(RemoteFailure(request.uri, status))
            }
        }
    }
}

fun S3.Bucket.Companion.Http(uri: Uri,
                             rawHttp: HttpHandler,
                             scope: AwsCredentialScope,
                             credentialsProvider: () -> AwsCredentials,
                             clock: Clock = Clock.systemDefaultZone(),
                             payloadMode: Payload.Mode = Payload.Mode.Signed) = object : S3.Bucket {
    private val http =
        ClientFilters.SetBaseUriFrom(uri)
            .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
            .then(rawHttp)

    override fun delete(key: BucketKey) = key.request(DELETE).call()
    override fun set(key: BucketKey, content: InputStream) = key.request(PUT).body(content).call()

    override fun get(key: BucketKey) = with(http(key.request(GET))) {
        when {
            status.successful -> Success(body.stream)
            status == NOT_FOUND -> Success(null)
            else -> Failure(RemoteFailure(key.request(GET).uri, status))
        }
    }

    override fun list(): Result<List<BucketKey>, RemoteFailure> {
        val request = Request(GET, "/")

        return with(http(request)) {
            when {
                status.successful -> Success(emptyList())
                else -> Failure(RemoteFailure(request.uri, status))
            }
        }
    }

    private fun Request.call() = with(http(this)) {
        when {
            status.successful -> Success(Unit)
            else -> Failure(RemoteFailure(uri, status))
        }
    }
}

private fun BucketKey.request(method: Method) = Request(method, "/$value")

internal val documentBuilderFactory by lazy {
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
}
