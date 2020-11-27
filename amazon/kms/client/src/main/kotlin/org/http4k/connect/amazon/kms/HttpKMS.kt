package org.http4k.connect.amazon.kms

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.http4k.filter.ClientFilters
import org.http4k.filter.Payload
import org.http4k.filters.AmazonRegionalJsonStack
import java.time.Clock

fun KMS.Companion.Http(scope: AwsCredentialScope,
                       credentialsProvider: () -> AwsCredentials,
                       rawHttp: HttpHandler = JavaHttpClient(),
                       clock: Clock = Clock.systemDefaultZone(),
                       payloadMode: Payload.Mode = Payload.Mode.Signed) = object : KMS {
    private val http = ClientFilters.AmazonRegionalJsonStack(scope,
        rawHttp,
        credentialsProvider,
        AwsService.of("kms"),
        clock,
        payloadMode)

    private val req = KMSJackson.autoBody<Any>().toLens()
}
