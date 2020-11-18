package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.secretsmanager.SecretsManagerJackson.auto
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import java.time.Clock

fun SecretsManager.Companion.Http(scope: AwsCredentialScope,
                                  credentialsProvider: () -> AwsCredentials,
                                  rawHttp: HttpHandler = JavaHttpClient(),
                                  clock: Clock = Clock.systemDefaultZone(),
                                  payloadMode: Payload.Mode = Payload.Mode.Signed) = object : SecretsManager {
    private val http =
        SetBaseUriFrom(Uri.of("https://secretsmanager.${scope.region}.amazonaws.com/"))
            .then(SetXForwardedHost())
            .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
            .then(rawHttp)

    private val req = Body.auto<Any>().toLens()
    private val getResp = Body.auto<GetSecretValue.Response>().toLens()

    override fun get(request: GetSecretValue.Request) =
        Uri.of("/").let {
            with(http(Request(GET, it).with(req of request))) {
                when {
                    status.successful -> Success(getResp(this))
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }

    private val putResp = Body.auto<PutSecretValue.Response>().toLens()

    override fun put(request: PutSecretValue.Request) =
        Uri.of("/").let {
            with(http(Request(POST, it).with(req of request))) {
                when {
                    status.successful -> Success(putResp(this))
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }
}
