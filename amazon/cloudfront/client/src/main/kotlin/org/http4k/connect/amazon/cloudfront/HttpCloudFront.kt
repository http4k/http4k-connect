package org.http4k.connect.amazon.cloudfront

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.awsCredentials
import org.http4k.connect.amazon.cloudfront.action.CloudFrontAction
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.Payload.Mode.Signed
import org.http4k.filter.RequestFilters.SetHeader
import java.lang.System.getenv
import java.time.Clock
import java.time.Clock.systemUTC

fun CloudFront.Companion.Http(
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = object : CloudFront {

    private val http = ClientFilters.SetHostFrom(Uri.of("https://cloudfront.amazonaws.com"))
        .then(ClientFilters.SetXForwardedHost())
        .then(SetHeader("Content-Type", APPLICATION_XML.value))
        .then(
            ClientFilters.AwsAuth(
                AwsCredentialScope("us-east-1", awsService.value),
                credentialsProvider, clock, Signed
            )
        )
        .then(rawHttp)

    override fun <R> invoke(action: CloudFrontAction<R>) = action.toResult(http(action.toRequest()))
}

fun CloudFront.Companion.Http(
    env: Map<String, String> = getenv(),
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = Http(env.awsCredentials(), rawHttp, clock)
