package org.http4k.connect.amazon

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.map
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.Payload
import org.http4k.format.AutoMarshallingJson
import java.time.Clock
import kotlin.reflect.KClass

class AmazonJsonApi(private val awsService: AwsService,
                    private val autoMarshallingJson: AutoMarshallingJson,
                    scope: AwsCredentialScope,
                    credentialsProvider: () -> AwsCredentials,
                    rawHttp: HttpHandler = JavaHttpClient(),
                    clock: Clock = Clock.systemDefaultZone(),
                    payloadMode: Payload.Mode = Payload.Mode.Signed) {
    private val http = SetBaseUriFrom(Uri.of("https://$awsService.${scope.region}.amazonaws.com/"))
        .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    fun <Req : Any, Resp : Any> operation(name: String, clazz: KClass<Resp>, request: Req, onBadRequest: (Uri, Response) -> Result<Resp?, RemoteFailure>) =
        Uri.of("/").let {
            with(http(Request(POST, it)
                .header("X-Amz-Target", "$awsService.$name")
                .replaceHeader("Content-Type", "application/x-amz-json-1.1")
                .body(autoMarshallingJson.asFormatString(request)))) {
                when {
                    status.successful -> Success(autoMarshallingJson.asA(bodyString(), clazz))
                    status == BAD_REQUEST -> onBadRequest(it, this)
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }
}

inline fun <Req : Any, reified Resp : Any> AmazonJsonApi.optional(operation: String, request: Req): Result<Resp?, RemoteFailure> = operation(operation, Resp::class, request, orNull())

inline fun <Req : Any, reified Resp : Any> AmazonJsonApi.required(operation: String, request: Req): Result<Resp, RemoteFailure> =
    when(val result: Result<Resp?, RemoteFailure> = operation(operation, Resp::class, request, orFailure())) {
        is Success<Resp?> -> result.map { it!! }
        is Failure<RemoteFailure> -> Failure(result.reason)
    }

fun <Resp : Any> orNull() = { _: Uri, _: Resp -> Success(null) }

fun orFailure() = { uri: Uri, resp: Response -> Failure(RemoteFailure(uri, resp.status)) }
