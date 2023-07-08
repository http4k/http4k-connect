package org.http4k.connect.amazon.firehose

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_CREDENTIALS
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.core.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.time.Clock

fun Firehose.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemDefaultZone()
) = object : Firehose {
    private val http = signAwsRequests(region, credentialsProvider, clock, Payload.Mode.Signed).then(rawHttp)

    override fun <R : Any> invoke(action: FirehoseAction<R>) = action.toResult(http(action.toRequest()))
}

/**
 * Convenience function to create a Firehose from a System environment
 */
fun Firehose.Companion.Http(
    env: Map<String, String> = System.getenv(),
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = java.time.Clock.systemUTC()
) = Http(Environment.from(env), http, clock)

/**
 * Convenience function to create a Firehose from an http4k Environment
 */
fun Firehose.Companion.Http(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = java.time.Clock.systemUTC()
) = Http(AWS_REGION(env), { AWS_CREDENTIALS(env) }, http, clock)
