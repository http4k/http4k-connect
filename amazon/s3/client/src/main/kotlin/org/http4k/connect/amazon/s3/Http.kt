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

    override fun delete(key: BucketKey) = Uri.of("/$key").let {
        with(http(Request(DELETE, it))) {
            when {
                status.successful -> Success(Unit)
                status == NOT_FOUND -> Success(null)
                else -> Failure(RemoteFailure(Request(DELETE, "/$key").uri, status))
            }
        }
    }

    override fun set(key: BucketKey, content: InputStream) = Uri.of("/$key").let {
        with(http(Request(PUT, it))) {
            when {
                status.successful -> Success(Unit)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun get(key: BucketKey) = Uri.of("/$key").let {
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
                status.successful -> Success(emptyList<BucketKey>())
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }
}

internal val documentBuilderFactory by lazy {
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
}
