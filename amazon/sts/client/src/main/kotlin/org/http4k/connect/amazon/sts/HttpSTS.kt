package org.http4k.connect.amazon.sts

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.sts.action.STSAction
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import java.time.Clock

fun STS.Companion.Http(region: Region,
                       credentialsProvider: () -> AwsCredentials,
                       rawHttp: HttpHandler = JavaHttpClient(),
                       clock: Clock = Clock.systemDefaultZone(),
                       payloadMode: Payload.Mode = Payload.Mode.Signed) = object : STS {
    private val http = SetHostFrom(Uri.of("https://sts.$region.amazonaws.com"))
        .then(SetXForwardedHost())
        .then(ClientFilters.AwsAuth(AwsCredentialScope(region.value, "sts"), credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    override fun <R> invoke(request: STSAction<R>) = request.toResult(http(request.toRequest()))
}
