package org.http4k.connect.amazon.s3.model

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.aws.AwsRequestPreSigner
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.s3.S3
import org.http4k.core.Headers
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.appendToPath
import java.time.Clock
import java.time.Duration

class S3BucketPreSigner(
    bucketName: BucketName,
    region: Region,
    credentialsProvider: CredentialsProvider,
    clock: Clock = Clock.systemUTC()
) {
    constructor(
        bucketName: BucketName,
        region: Region,
        credentials: AwsCredentials,
        clock: Clock = Clock.systemUTC()
    ) : this(
        bucketName = bucketName,
        region = region,
        credentialsProvider = { credentials },
        clock = clock
    )

    val preSigner = AwsRequestPreSigner(
        credentialsProvider = credentialsProvider,
        scope = AwsCredentialScope(region.value, S3.awsService.value),
        clock = clock
    )

    private val bucketUri = let {
        val pathPrefix = if (bucketName.requiresPathStyleApi()) "/$bucketName" else ""
        val subdomain = if (bucketName.requiresPathStyleApi()) "" else "$bucketName."
        Uri.of("https://$subdomain${S3.awsService}.$region.amazonaws.com").path(pathPrefix)
    }

    fun get(key: BucketKey, duration: Duration, headers: Headers = emptyList()) = preSigner(
        Request(GET, bucketUri.appendToPath(key.value)).headers(headers),
        duration
    )

    fun put(key: BucketKey, duration: Duration, headers: Headers = emptyList()) = preSigner(
        Request(PUT, bucketUri.appendToPath(key.value)).headers(headers),
        duration
    )

    fun delete(key: BucketKey, duration: Duration, headers: Headers = emptyList()) = preSigner(
        Request(DELETE, bucketUri.appendToPath(key.value)).headers(headers),
        duration
    )
}
