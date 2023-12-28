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
import java.time.Clock
import java.time.Duration

class S3BucketPreSigner(
    bucketName: BucketName,
    region: Region,
    credentialsProvider: CredentialsProvider,
    clock: Clock = Clock.systemDefaultZone()
) {
    constructor(
        bucketName: BucketName,
        region: Region,
        credentials: AwsCredentials,
        clock: Clock = Clock.systemDefaultZone()
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

    private val bucketUri = Uri.of("https://$bucketName.${S3.awsService}.$region.amazonaws.com")

    fun get(key: BucketKey, duration: Duration, headers: Headers = emptyList()) = preSigner(
        Request(GET, bucketUri.path("/$key")).headers(headers),
        duration
    )

    fun put(key: BucketKey, duration: Duration, headers: Headers = emptyList()) = preSigner(
        Request(PUT, bucketUri.path("/$key")).headers(headers),
        duration
    )

    fun delete(key: BucketKey, duration: Duration, headers: Headers = emptyList()) = preSigner(
        Request(DELETE, bucketUri.path("/$key")).headers(headers),
        duration
    )
}
