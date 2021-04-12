package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_CREDENTIALS
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.systemsmanager.action.SystemsManagerAction
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.lang.System.getenv
import java.time.Clock
import java.time.Clock.systemUTC

/**
 * Standard HTTP implementation of SystemsManager
 */
fun SystemsManager.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = object : SystemsManager {
    private val signedHttp = signAwsRequests(region, credentialsProvider, clock, Payload.Mode.Signed).then(http)
    override fun <R : Any> invoke(action: SystemsManagerAction<R>) = action.toResult(signedHttp(action.toRequest()))
}

/**
 * Convenience function to create a SystemsManager from a System environment
 */
fun SystemsManager.Companion.Http(
    env: Map<String, String> = getenv(),
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = Http(Environment.from(env), http, clock)

/**
 * Convenience function to create a SystemsManager from an http4k Environment
 */
fun SystemsManager.Companion.Http(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = Http(AWS_REGION(env), { AWS_CREDENTIALS(env) }, http, clock)
