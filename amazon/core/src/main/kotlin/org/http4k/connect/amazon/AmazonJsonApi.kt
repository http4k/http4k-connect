package org.http4k.connect.amazon

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.Payload
import org.http4k.format.AutoMarshalling
import java.time.Clock
import kotlin.reflect.KClass

class AmazonJsonApi(private val awsService: AwsService,
                    private val autoMarshalling: AutoMarshalling,
                    scope: AwsCredentialScope,
                    credentialsProvider: () -> AwsCredentials,
                    rawHttp: HttpHandler = JavaHttpClient(),
                    clock: Clock = Clock.systemDefaultZone(),
                    payloadMode: Payload.Mode = Payload.Mode.Signed,
                    private val httpAwsService: AwsService = awsService) {
    private val http = SetBaseUriFrom(Uri.of("https://$awsService.${scope.region}.amazonaws.com"))
        .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    fun <Req : Any, Resp : Any> operation(name: String, clazz: KClass<Resp>, request: Req) =
        Uri.of("/").let {
            with(http(Request(POST, it)
                .header("X-Amz-Target", "$httpAwsService.$name")
                .replaceHeader("Content-Type", "application/x-amz-json-1.1")
                .body(autoMarshalling.asFormatString(request)))) {
                when {
                    status.successful -> Success(autoMarshalling.asA(bodyString(), clazz))
                    else -> Failure(RemoteFailure(POST, it, status, bodyString()))
                }
            }
        }
}

inline operator fun <Req : Any, reified Resp : Any> AmazonJsonApi.invoke(operation: String, request: Req): Result<Resp, RemoteFailure> = when(val result: Result<Resp, RemoteFailure> = operation(operation, Resp::class, request)) {
        is Success<Resp> -> result
        is Failure<RemoteFailure> -> Failure(result.reason)
    }

