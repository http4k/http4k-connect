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
    private val http = SetBaseUriFrom(Uri.of("https://s3.${scope.region}.amazonaws.com/"))
        .then(SetXForwardedHost())
        .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    override operator fun invoke(request: ListBucketsRequest) = Uri.of("/").let {
        with(http(Request(GET, it))) {
            when {
                status.successful -> {
                    val buckets = documentBuilderFactory.parse(body.stream).getElementsByTagName("Name")
                    val items = (0 until buckets.length).map { BucketName.of(buckets.item(it).textContent) }
                    Success(if (items.isNotEmpty()) Listing.Unpaged(items) else Listing.Empty)
                }
                else -> Failure(RemoteFailure(GET, it, status))
            }
        }
    }

    override operator fun invoke(request: CreateBucketRequest) = Uri.of("/${request.bucketName}").let {
        with(http(Request(PUT, it).body("""<?xml version="1.0" encoding="UTF-8"?>
<CreateBucketConfiguration xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
   <LocationConstraint>${scope.region}</LocationConstraint>
</CreateBucketConfiguration>"""))) {
            when {
                status.successful -> Success(Unit)
                else -> Failure(RemoteFailure(PUT, it, status))
            }
        }
    }

    override operator fun invoke(request: DeleteBucketRequest) = Uri.of("/${request.bucketName}").let {
        with(http(Request(DELETE, it))) {
            when {
                status.successful -> Success(Unit)
                status == NOT_FOUND -> Success(null)
                else -> Failure(RemoteFailure(DELETE, it, status))
            }
        }
    }
}
