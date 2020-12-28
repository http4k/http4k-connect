package org.http4k.connect.amazon.lambda

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.lambda.action.LambdaAction
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import java.time.Clock

fun Lambda.Companion.Http(scope: AwsCredentialScope,
                          credentialsProvider: () -> AwsCredentials,
                          rawHttp: HttpHandler = JavaHttpClient(),
                          clock: Clock = Clock.systemDefaultZone(),
                          payloadMode: Payload.Mode = Payload.Mode.Signed) = object : Lambda {

    private val http = SetBaseUriFrom(Uri.of("https://lambda.${scope.region}.amazonaws.com"))
        .then(SetXForwardedHost())
        .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    override fun <RESP : Any>
        invoke(request: LambdaAction<RESP>) = request.toResult(http(request.toRequest()))
}
