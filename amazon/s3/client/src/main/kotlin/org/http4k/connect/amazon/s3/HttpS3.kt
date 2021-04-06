package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.awsCredentials
import org.http4k.connect.amazon.s3.action.S3Action
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import java.lang.System.getenv
import java.time.Clock
import java.time.Clock.systemUTC

fun S3.Companion.Http(
    credentialsProvider: () -> AwsCredentials,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC(),
    payloadMode: Payload.Mode = Payload.Mode.Signed
) = object : S3 {
    private val signedHttp = SetHostFrom(Uri.of("https://s3.amazonaws.com"))
        .then(SetXForwardedHost())
        .then(
            ClientFilters.AwsAuth(
                AwsCredentialScope("us-east-1", awsService.value),
                credentialsProvider, clock, payloadMode
            )
        )
        .then(http)

    override fun <R> invoke(action: S3Action<R>) = action.toResult(signedHttp(action.toRequest()))
}

fun S3.Companion.Http(
    env: Map<String, String> = getenv(),
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC(),
    payloadMode: Payload.Mode = Payload.Mode.Signed
) = Http(env.awsCredentials(), http, clock, payloadMode)
