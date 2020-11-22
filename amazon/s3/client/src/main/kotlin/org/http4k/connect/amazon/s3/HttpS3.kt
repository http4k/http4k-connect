package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.Listing
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.documentBuilderFactory
import org.http4k.core.HttpHandler
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
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import java.time.Clock

fun S3.Companion.Http(scope: AwsCredentialScope,
                      credentialsProvider: () -> AwsCredentials,
                      rawHttp: HttpHandler = JavaHttpClient(),
                      clock: Clock = Clock.systemDefaultZone(),
                      payloadMode: Payload.Mode = Payload.Mode.Signed) = object : S3 {
    private val regionClient = scope.clientFor(Uri.of("https://s3.amazonaws.com/"))
    private val globalClient = scope.copy(region = "us-east-1").clientFor(Uri.of("https://s3.amazonaws.com/"))

    private fun AwsCredentialScope.clientFor(uri: Uri) = SetBaseUriFrom(uri)
        .then(SetXForwardedHost())
        .then(ClientFilters.AwsAuth(this, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    override fun buckets() = Uri.of("/").let {
        with(globalClient(Request(GET, it))) {
            when {
                status.successful -> {
                    val buckets = documentBuilderFactory.parse(body.stream).getElementsByTagName("Name")
                    val items = (0 until buckets.length).map { BucketName(buckets.item(it).textContent) }
                    Success(if (items.isNotEmpty()) Listing.Unpaged(items) else Listing.Empty)
                }
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun create(bucketName: BucketName) = Uri.of("/$bucketName").let {
        with(globalClient(Request(PUT, it))) {
            when {
                status.successful -> Success(Unit)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun delete(bucketName: BucketName) = Uri.of("/$bucketName").let {
        with(globalClient(Request(DELETE, it))) {
            when {
                status.successful -> Success(Unit)
                status == NOT_FOUND -> Success(null)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }
}
