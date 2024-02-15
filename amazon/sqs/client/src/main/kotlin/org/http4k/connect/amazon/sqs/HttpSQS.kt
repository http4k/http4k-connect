package org.http4k.connect.amazon.sqs

import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.Environment
import org.http4k.connect.amazon.core.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.Payload.Mode.Signed
import java.time.Clock

/**
 * Standard HTTP implementation of SQS
 */
fun SQS.Companion.Http(
    region: Region,
    credentialsProvider: CredentialsProvider,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    endpoint: Uri? = null,
) = object : SQS {
    private val signedHttp = signAwsRequests(region, credentialsProvider, clock, Signed, endpoint).then(http)

    override fun <R> invoke(action: SQSAction<R>) = action.toResult(signedHttp(action.toRequest()))
}

/**
 * Convenience function to create a SQS from a System environment
 */
fun SQS.Companion.Http(
    env: Map<String, String> = System.getenv(),
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    endpoint: Uri? = null,
    credentialsProvider: CredentialsProvider = CredentialsProvider.Environment(env)
) = Http(Environment.from(env), http, clock, endpoint, credentialsProvider)

/**
 * Convenience function to create a SQS from an http4k Environment
 */
fun SQS.Companion.Http(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    endpoint: Uri? = null,
    credentialsProvider: CredentialsProvider = CredentialsProvider.Environment(env)
) = Http(AWS_REGION(env), credentialsProvider, http, clock, endpoint)
