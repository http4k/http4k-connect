package org.http4k.connect.amazon.kms

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.kms.action.KMSAction
import org.http4k.connect.amazon.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import java.time.Clock

fun KMS.Companion.Http(region: Region,
                       credentialsProvider: () -> AwsCredentials,
                       rawHttp: HttpHandler = JavaHttpClient(),
                       clock: Clock = Clock.systemDefaultZone(),
                       payloadMode: Payload.Mode = Payload.Mode.Signed) = object : KMS {

    private val http = SetBaseUriFrom(Uri.of("https://kms.$region.amazonaws.com"))
        .then(SetXForwardedHost())
        .then(ClientFilters.AwsAuth(AwsCredentialScope(region.value, "kms"), credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    override fun <R : Any> invoke(request: KMSAction<R>) = request.toResult(http(request.toRequest()))
}
