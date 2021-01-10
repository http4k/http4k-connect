package org.http4k.connect.amazon.kms

import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.kms.action.KMSAction
import org.http4k.connect.amazon.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.Payload
import java.time.Clock

fun KMS.Companion.Http(
    region: Region,
    credentialsProvider: () -> AwsCredentials,
    rawHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemDefaultZone(),
    payloadMode: Payload.Mode = Payload.Mode.Signed
) = object : KMS {
    private val http = signAwsRequests(region, credentialsProvider, clock, payloadMode).then(rawHttp)

    override fun <R : Any> invoke(action: KMSAction<R>) = action.toResult(http(action.toRequest()))
}
