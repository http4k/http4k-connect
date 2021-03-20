package org.http4k.connect.amazon.sts

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.awsCredentials
import org.http4k.connect.amazon.awsRegion
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.sts.action.STSAction
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.lang.System.getenv
import java.time.Clock
import java.time.Clock.systemUTC

fun STS.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = object : STS {
    private val http = signAwsRequests(region, credentialsProvider, clock, Payload.Mode.Signed).then(rawHttp)

    override fun <R> invoke(action: STSAction<R>) = action.toResult(http(action.toRequest()))
}

fun STS.Companion.Http(
    env: Map<String, String> = getenv(),
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = systemUTC()
) = Http(env.awsRegion(), env.awsCredentials(), rawHttp, clock)
