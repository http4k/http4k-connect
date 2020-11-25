package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import java.time.Clock

fun SystemsManager.Companion.Http(scope: AwsCredentialScope,
                                  credentialsProvider: () -> AwsCredentials,
                                  rawHttp: HttpHandler = JavaHttpClient(),
                                  clock: Clock = Clock.systemDefaultZone(),
                                  payloadMode: Payload.Mode = Payload.Mode.Signed) = object : SystemsManager {
    private val http =
        SetBaseUriFrom(Uri.of("https://systemsmanager.${scope.region}.amazonaws.com/"))
            .then(SetXForwardedHost())
            .then(Filter { next ->
                {
                    next(it.replaceHeader("Content-Type", "application/x-amz-json-1.1"))
                }
            })
            .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
            .then(rawHttp)

    private val req = SystemsManagerJackson.autoBody<Any>().toLens()

}
