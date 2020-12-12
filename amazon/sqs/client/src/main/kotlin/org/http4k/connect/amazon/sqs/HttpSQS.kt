package org.http4k.connect.amazon.sqs

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.core.ContentType.Companion.APPLICATION_FORM_URLENCODED
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import org.http4k.lens.Header.CONTENT_TYPE
import java.time.Clock

fun SQS.Companion.Http(scope: AwsCredentialScope,
                       credentialsProvider: () -> AwsCredentials,
                       rawHttp: HttpHandler = JavaHttpClient(),
                       clock: Clock = Clock.systemDefaultZone(),
                       payloadMode: Payload.Mode = Payload.Mode.Signed
) = object : SQS {
    private val http = SetBaseUriFrom(Uri.of("https://sqs.${scope.region}.amazonaws.com"))
        .then(SetXForwardedHost())
        .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    //            .body("""Action=SendMessage&Version=2012-11-05&MessageBody=asd""")

    override fun invoke(request: SendMessage): Result<Unit, RemoteFailure> {
        val base = listOf(
            "Action" to "SendMessage",
            "MessageBody" to request.payload,
            "Version" to "2012-11-05"
        )

        val listOf = base + listOf<Pair<String, String>>()

        val response = http(listOf.fold(Request(POST, Uri.of(""))
                .with(CONTENT_TYPE of APPLICATION_FORM_URLENCODED)) { acc, it ->
                acc.form(it.first, it.second)
            })

        return when {
            response.status.successful -> Success(Unit)
            else -> Failure(RemoteFailure(POST, Uri.of(""), response.status))
        }
    }
}
