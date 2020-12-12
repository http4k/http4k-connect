package org.http4k.connect.amazon.kms

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.Payload
import java.time.Clock

fun KMS.Companion.Http(scope: AwsCredentialScope,
                       credentialsProvider: () -> AwsCredentials,
                       rawHttp: HttpHandler = JavaHttpClient(),
                       clock: Clock = Clock.systemDefaultZone(),
                       payloadMode: Payload.Mode = Payload.Mode.Signed) = object : KMS {

    private val http = ClientFilters.SetBaseUriFrom(Uri.of("https://kms.${scope.region}.amazonaws.com"))
        .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    override fun <R : Any> invoke(request: KMSAction<R>) = request.toResult(http(request.toRequest()))
}
