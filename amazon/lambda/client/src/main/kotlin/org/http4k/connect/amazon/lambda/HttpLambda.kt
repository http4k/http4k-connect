package org.http4k.connect.amazon.lambda

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.lambda.action.LambdaAction
import org.http4k.connect.amazon.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.time.Clock

fun Lambda.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemDefaultZone(),
    payloadMode: Payload.Mode = Payload.Mode.Signed
) = object : Lambda {
    private val http = signAwsRequests(region, credentialsProvider, clock, payloadMode).then(rawHttp)

    override fun <RESP : Any> invoke(action: LambdaAction<RESP>) = action.toResult(http(action.toRequest()))
}
