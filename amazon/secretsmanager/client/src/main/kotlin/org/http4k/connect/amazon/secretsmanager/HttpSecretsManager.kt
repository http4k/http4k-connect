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

    override fun create(request: CreateSecret.Request) =
        required<CreateSecret.Request, CreateSecret.Response>("CreateSecret", request)

    override fun delete(request: DeleteSecret.Request) =
        optional<DeleteSecret.Request, DeleteSecret.Response>("DeleteSecret", request)

    override fun list(request: ListSecrets.Request) =
        required<ListSecrets.Request, ListSecrets.Response>("ListSecrets", request)

    override fun lookup(request: GetSecret.Request) =
        optional<GetSecret.Request, GetSecret.Response>("GetSecret", request)

    override fun update(request: UpdateSecret.Request) =
        optional<UpdateSecret.Request, UpdateSecret.Response>("UpdateSecret", request)

    private inline fun <Req : Any, reified Resp : Any> optional(operation: String, request: Req) =
        Uri.of("/").let {
            with(http(Request(POST, it)
                .header("X-Amz-Target", "secretsmanager.$operation")
                .with(req of request))) {
                when {
                    status.successful -> Success(Body.auto<Resp>().toLens()(this))
                    status == BAD_REQUEST -> Success(null)
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }

    private inline fun <Req : Any, reified Resp : Any> required(operation: String, request: Req) =
        Uri.of("/").let {
            with(http(Request(POST, it)
                .header("X-Amz-Target", "secretsmanager.$operation")
                .with(req of request))) {
                when {
                    status.successful -> Success(Body.auto<Resp>().toLens()(this))
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }
}
