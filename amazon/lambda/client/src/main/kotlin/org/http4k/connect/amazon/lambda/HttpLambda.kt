package org.http4k.connect.amazon.lambda

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.awsCredentials
import org.http4k.connect.amazon.awsRegion
import org.http4k.connect.amazon.lambda.action.LambdaAction
import org.http4k.connect.amazon.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.lang.System.getenv
import java.time.Clock
import java.time.Clock.systemUTC

fun Lambda.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = object : Lambda {
    private val http = signAwsRequests(region, credentialsProvider, clock, Payload.Mode.Signed).then(rawHttp)

    override fun <RESP : Any> invoke(action: LambdaAction<RESP>) = action.toResult(http(action.toRequest()))
}

fun Lambda.Companion.Http(
    env: Map<String, String> = getenv(),
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = Http(env.awsRegion(), env.awsCredentials(), rawHttp, clock)
