package org.http4k.connect.amazon

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.with
import org.http4k.filter.ClientFilters
import org.http4k.filter.Payload
import org.http4k.filters.AmazonRegionalJsonStack
import org.http4k.format.ConfigurableJackson
import java.time.Clock

class AmazonJsonApi(val awsService: AwsService,
                    scope: AwsCredentialScope,
                    credentialsProvider: () -> AwsCredentials,
                    val jackson: ConfigurableJackson,
                    rawHttp: HttpHandler = JavaHttpClient(),
                    clock: Clock = Clock.systemDefaultZone(),
                    payloadMode: Payload.Mode = Payload.Mode.Signed) {
    val http = ClientFilters.AmazonRegionalJsonStack(scope,
        rawHttp,
        credentialsProvider,
        awsService,
        clock,
        payloadMode)

    val req = jackson.autoBody<Any>().toLens()

    inline fun <Req : Any, reified Resp : Any> optional(operation: String, request: Req) =
        Uri.of("/").let {
            with(http(Request(Method.POST, it)
                .header("X-Amz-Target", "$awsService.$operation")
                .with(req of request))) {
                when {
                    status.successful -> Success(jackson.autoBody<Resp>().toLens()(this))
                    status == org.http4k.core.Status.BAD_REQUEST -> Success(null)
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }

    inline fun <Req : Any, reified Resp : Any> required(operation: String, request: Req) =
        Uri.of("/").let {
            with(http(Request(Method.POST, it)
                .header("X-Amz-Target", "$awsService.$operation")
                .with(req of request))) {
                when {
                    status.successful -> Success(jackson.autoBody<Resp>().toLens()(this))
                    else -> Failure(RemoteFailure(it, status))
                }
            }
        }

}
