package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.secretsmanager.SecretsManagerJackson.auto
import org.http4k.core.Body
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
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
            .then(Filter { next ->
                {
                    next(it.replaceHeader("Content-Type", "application/x-amz-json-1.1"))
                }
            })
            .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
            .then(rawHttp)

    private val req = SecretsManagerJackson.autoBody<Any>().toLens()

    override fun lookup(request: GetSecret.Request) =
        Uri.of("/").let {
            val resp = Body.auto<GetSecret.Response>().toLens()

            with(http(Request(POST, it)
                .header("X-Amz-Target", "secretsmanager.GetSecretValue")
                .with(req of request))) {
                when {
                    status.successful -> Success(resp(this))
                    status == BAD_REQUEST -> Success(null)
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }

    override fun create(request: CreateSecret.Request) =
        Uri.of("/").let {
            val resp = Body.auto<CreateSecret.Response>().toLens()

            with(http(Request(POST, it)
                .header("X-Amz-Target", "secretsmanager.PutSecretValue")
                .with(req of request))) {
                when {
                    status.successful -> Success(resp(this))
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }

    override fun update(request: UpdateSecret.Request) = Uri.of("/").let {
        val resp = Body.auto<UpdateSecret.Response>().toLens()

        with(http(Request(POST, it)
            .header("X-Amz-Target", "secretsmanager.UpdateSecret")
            .with(req of request))) {
            when {
                status.successful -> Success(resp(this))
                status == BAD_REQUEST -> Success(null)
                else -> Failure(RemoteFailure(it, status))
            }
        }
    }

    override fun delete(request: DeleteSecret.Request) =
        Uri.of("/").let {
            val resp = Body.auto<DeleteSecret.Response>().toLens()

            with(http(Request(POST, it)
                .header("X-Amz-Target", "secretsmanager.DeleteSecret")
                .with(req of request))) {
                when {
                    status.successful -> Success(resp(this))
                    status == BAD_REQUEST -> Success(null)
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }
}
