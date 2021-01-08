package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.s3.action.S3Action
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.Payload
import java.time.Clock

fun S3.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemDefaultZone(),
    payloadMode: Payload.Mode = Payload.Mode.Signed
) = object : S3 {
    val http = ClientFilters.SetHostFrom(Uri.of("https://s3.amazonaws.com"))
        .then(ClientFilters.SetXForwardedHost())
        .then(
            ClientFilters.AwsAuth(
                AwsCredentialScope(region.value, awsService.value),
                credentialsProvider, clock, payloadMode
            )
        )
        .then(rawHttp)

    override fun <R> invoke(action: S3Action<R>) = action.toResult(http(action.toRequest()))
}
