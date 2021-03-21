package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.awsCredentials
import org.http4k.connect.amazon.awsRegion
import org.http4k.connect.amazon.model.Region
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
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = object : SystemsManager {
    private val http = signAwsRequests(region, credentialsProvider, clock, Payload.Mode.Signed).then(rawHttp)
    override fun <R : Any> invoke(action: SystemsManagerAction<R>) = action.toResult(http(action.toRequest()))
}

fun SystemsManager.Companion.Http(
    env: Map<String, String> = getenv(),
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = Http(env.awsRegion(), env.awsCredentials(), rawHttp, clock)
