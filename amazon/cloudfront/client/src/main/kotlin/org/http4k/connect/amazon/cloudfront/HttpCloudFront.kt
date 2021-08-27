package org.http4k.connect.amazon.cloudfront

import org.http4k.aws.AwsCredentialScope
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.Environment
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

/**
 * Standard HTTP implementation of CloudFront
 */
fun CloudFront.Companion.Http(
    credentialsProvider: CredentialsProvider,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = object : CloudFront {

    private val signedHttp = ClientFilters.SetHostFrom(Uri.of("https://cloudfront.amazonaws.com"))
        .then(ClientFilters.SetXForwardedHost())
        .then(SetHeader("Content-Type", APPLICATION_XML.value))
        .then(
            ClientFilters.AwsAuth(
                AwsCredentialScope("us-east-1", awsService.value),
                credentialsProvider, clock, Signed
            )
        )
        .then(http)

    override fun <R> invoke(action: CloudFrontAction<R>) = action.toResult(signedHttp(action.toRequest()))
}

/**
 * Convenience function to create a CloudFront from a System environment
 */
fun CloudFront.Companion.Http(
    env: Map<String, String> = getenv(),
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = Http(Environment.from(env), http, clock)

/**
 * Convenience function to create a CloudFront from an http4k Environment
 */
fun CloudFront.Companion.Http(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = Http(CredentialsProvider.Environment(env), http, clock)
