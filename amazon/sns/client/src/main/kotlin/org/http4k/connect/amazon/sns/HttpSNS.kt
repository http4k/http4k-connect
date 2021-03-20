package org.http4k.connect.amazon.sns

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.awsCredentials
import org.http4k.connect.amazon.awsRegion
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.sns.action.SNSAction
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.time.Clock

fun SNS.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC()
) = object : SNS {
    private val http = signAwsRequests(region, credentialsProvider, clock, Payload.Mode.Signed).then(rawHttp)

    override fun <R> invoke(action: SNSAction<R>) = action.toResult(http(action.toRequest()))
}

fun SNS.Companion.Http(
    env: Map<String, String> = System.getenv(),
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC()
) = Http(env.awsRegion(), env.awsCredentials(), rawHttp, clock)
