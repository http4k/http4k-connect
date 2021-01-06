package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.systemsmanager.action.SystemsManagerAction
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.time.Clock

fun SystemsManager.Companion.Http(region: Region,
                                  credentialsProvider: () -> AwsCredentials,
                                  rawHttp: HttpHandler = JavaHttpClient(),
                                  clock: Clock = Clock.systemDefaultZone(),
                                  payloadMode: Payload.Mode = Payload.Mode.Signed) = object : SystemsManager {
    private val http = signAwsRequests(region, credentialsProvider, clock, payloadMode).then(rawHttp)
    override fun <R : Any> invoke(action: SystemsManagerAction<R>) = action.toResult(http(action.toRequest()))
}
