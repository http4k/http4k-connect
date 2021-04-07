package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.awsCredentials
import org.http4k.connect.amazon.awsRegion
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.systemsmanager.action.SystemsManagerAction
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.lang.System.getenv
import java.time.Clock
import java.time.Clock.systemUTC

fun SystemsManager.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = object : SystemsManager {
    private val signedHttp = signAwsRequests(region, credentialsProvider, clock, Payload.Mode.Signed).then(http)
    override fun <R : Any> invoke(action: SystemsManagerAction<R>) = action.toResult(signedHttp(action.toRequest()))
}

fun SystemsManager.Companion.Http(
    env: Map<String, String> = getenv(),
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = Http(env.awsRegion(), env.awsCredentials(), http, clock)
