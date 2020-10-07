package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Uri
import org.http4k.core.filters.SetXForwardedHost
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.Payload
import java.time.Clock

fun S3.Companion.Http(rawHttp: HttpHandler,
                      scope: AwsCredentialScope,
                      credentialsProvider: () -> AwsCredentials,
                      clock: Clock = Clock.systemDefaultZone(),
                      payloadMode: Payload.Mode = Payload.Mode.Signed) = object : S3 {
    private val http =
        ClientFilters.SetBaseUriFrom(Uri.of("https://s3.amazonaws.com/"))
            .then(ClientFilters.SetXForwardedHost())
            .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
            .then(rawHttp)

    override fun buckets() = Uri.of("/").let {
        with(http(Request(GET, it))) {
            when {
                status.successful -> {
                    val buckets = documentBuilderFactory.parse(body.stream).getElementsByTagName("Name")
                    Success((0 until buckets.length).map { BucketName(buckets.item(it).textContent) })
                }
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun create(bucketName: BucketName) = Uri.of("/$bucketName").let {
        with(http(Request(PUT, it))) {
            when {
                status.successful -> Success(Unit)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun delete(bucketName: BucketName) = Uri.of("/$bucketName").let {
        with(http(Request(DELETE, it))) {
            when {
                status.successful -> Success(Unit)
                status == NOT_FOUND -> Success(null)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }
}
