package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.Listing
import org.http4k.connect.RemoteFailure
import org.http4k.core.HttpHandler
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.Payload
import java.io.InputStream
import java.time.Clock

fun S3.Bucket.Companion.Http(bucketName: BucketName,
                             rawHttp: HttpHandler,
                             scope: AwsCredentialScope,
                             credentialsProvider: () -> AwsCredentials,
                             clock: Clock = Clock.systemDefaultZone(),
                             payloadMode: Payload.Mode = Payload.Mode.Signed) = object : S3.Bucket {
    private val http =
        ClientFilters.SetBaseUriFrom(Uri.of("https://$bucketName.s3.amazonaws.com/"))
            .then(ClientFilters.SetXForwardedHost())
            .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
            .then(rawHttp)

    override fun create() = Uri.of("/").let {
        with(http(Request(PUT, it))) {
            when {
                status.successful -> Success(Unit)
                status == CONFLICT -> Success(Unit)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun delete() = Uri.of("/").let {
        with(http(Request(DELETE, it))) {
            when {
                status.successful -> Success(Unit)
                status == NOT_FOUND -> Success(null)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun copy(originalKey: BucketKey, newKey: BucketKey) = Uri.of("/$newKey").let {
        with(http(Request(PUT, it).header("x-amz-copy-source", "$bucketName/$originalKey"))) {
            when {
                status.successful -> Success(Unit)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun delete(key: BucketKey) = Uri.of("/$key").let {
        with(http(Request(DELETE, it))) {
            when {
                status.successful -> Success(Unit)
                status == NOT_FOUND -> Success(null)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override operator fun set(key: BucketKey, content: InputStream) = Uri.of("/$key").let {
        with(http(Request(PUT, it).body(content))) {
            when {
                status.successful -> Success(Unit)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override operator fun get(key: BucketKey) = Uri.of("/$key").let {
        with(http(Request(GET, it))) {
            when {
                status.successful -> Success(body.stream)
                status == NOT_FOUND -> Success(null)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun list() = Uri.of("/").let {
        with(http(Request(GET, it))) {
            when {
                status.successful -> {
                    val keys = documentBuilderFactory.parse(body.stream).getElementsByTagName("Key")
                    val items = (0 until keys.length).map { BucketKey(keys.item(it).textContent) }
                    Success(if (items.isNotEmpty()) Listing.Unpaged(items) else Listing.Empty)
                }
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }
}
