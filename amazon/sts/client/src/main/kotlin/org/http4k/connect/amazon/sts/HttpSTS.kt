package org.http4k.connect.amazon.sts

import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.Environment
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.sts.action.AssumeRoleWithWebIdentity
import org.http4k.connect.amazon.sts.action.STSAction
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload.Mode.Signed
import java.lang.System.getenv
import java.time.Clock
import java.time.Clock.systemUTC

/**
 * Standard HTTP implementation of STS
 */
fun STS.Companion.Http(
    region: Region,
    credentialsProvider: CredentialsProvider,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = object : STS {
    private val signedHttp = signAwsRequests(region, credentialsProvider, clock, Signed).then(http)
    private val unauthedHttp = setHostForAwsService(region).then(http)

    override fun <R> invoke(action: STSAction<R>) =
        action.toResult(
            when (action) {
                is AssumeRoleWithWebIdentity -> unauthedHttp(action.toRequest())
                else -> signedHttp(action.toRequest())
            }
        )
}

/**
 * Convenience function to create a STS from a System environment
 */
fun STS.Companion.Http(
    env: Map<String, String> = getenv(),
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC(),
    credentialsProvider: CredentialsProvider = CredentialsProvider.Environment(env)
) = Http(Environment.from(env), http, clock, credentialsProvider)


/**
 * Convenience function to create a STS from an http4k Environment
 */
fun STS.Companion.Http(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC(),
    credentialsProvider: CredentialsProvider = CredentialsProvider.Environment(env)
) = Http(AWS_REGION(env), credentialsProvider, http, clock)
