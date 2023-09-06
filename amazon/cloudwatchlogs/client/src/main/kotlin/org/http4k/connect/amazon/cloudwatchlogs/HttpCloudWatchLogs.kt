package org.http4k.connect.amazon.cloudwatchlogs

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_CREDENTIALS
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.core.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload.Mode.Signed
import java.time.Clock

fun CloudWatchLogs.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemDefaultZone()
) = object : CloudWatchLogs {
    private val http = signAwsRequests(region, credentialsProvider, clock, Signed).then(rawHttp)

    override fun <R : Any> invoke(action: CloudWatchLogsAction<R>) = action.toResult(http(action.toRequest()))
}

/**
 * Convenience function to create a CloudWatchLogs from a System environment
 */
fun CloudWatchLogs.Companion.Http(
    env: Map<String, String> = System.getenv(),
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC()
) = Http(Environment.from(env), http, clock)

/**
 * Convenience function to create a CloudWatchLogs from an http4k Environment
 */
fun CloudWatchLogs.Companion.Http(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC()
) = Http(AWS_REGION(env), { AWS_CREDENTIALS(env) }, http, clock)
