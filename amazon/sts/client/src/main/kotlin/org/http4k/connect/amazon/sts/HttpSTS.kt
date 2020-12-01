package org.http4k.connect.amazon.sts

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AccessKeyId
import org.http4k.connect.amazon.model.AssumeRoleResponse
import org.http4k.connect.amazon.model.AssumeRoleResult
import org.http4k.connect.amazon.model.AssumedRoleUser
import org.http4k.connect.amazon.model.Credentials
import org.http4k.connect.amazon.model.ResponseMetadata
import org.http4k.connect.amazon.model.RoleId
import org.http4k.connect.amazon.model.SecretAccessKey
import org.http4k.connect.amazon.model.SessionToken
import org.http4k.connect.amazon.model.documentBuilderFactory
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.filter.Payload
import java.time.Clock
import java.time.Instant

fun STS.Http(scope: AwsCredentialScope,
             credentialsProvider: () -> AwsCredentials,
             rawHttp: HttpHandler = JavaHttpClient(),
             clock: Clock = Clock.systemDefaultZone(),
             payloadMode: Payload.Mode = Payload.Mode.Signed) = object : STS {

    private val http = SetBaseUriFrom(Uri.of("https://sts.${scope.region}.amazonaws.com/"))
        .then(SetXForwardedHost())
        .then(ClientFilters.AwsAuth(scope, credentialsProvider, clock, payloadMode))
        .then(rawHttp)

    override fun assumeRole(request: AssumeRole.Request): Result<AssumeRole.Response, RemoteFailure> {

        val base = listOf(
            "Action" to "AssumeRole",
            "RoleSessionName" to request.RoleSessionName,
            "RoleArn" to request.RoleArn.value
        )

        val policies = request.PolicyArns?.mapIndexed { index, next ->
            "PolicyArns.member.${index}.arn" to next.value
        }

        val tags = request.Tags?.flatMapIndexed { index, next ->
            listOf("Tags.member.${index}.Key" to next.Key, "Tags.member.${index}.Value" to next.Value)
        }

        val transitiveTags = request.TransitiveTagKeys?.mapIndexed() { index, next ->
            "TransitiveTagKeys.member.${index}" to next
        }

        val other: List<Pair<String, String>> = listOfNotNull(
            request.ExternalId?.let { "ExternalId" to it },
            request.Policy?.let { "Policy" to it },
            request.DurationSeconds?.let { "DurationSeconds" to it.seconds.toString() },
        )

        val uri = Uri.of("/")
        val response = http(listOfNotNull(base, policies, tags, transitiveTags, other)
            .flatten().fold(Request(POST, uri)) { acc, it ->
                acc.form(it.first, it.second)
            })

        when {
            response.status.successful -> response.parse()
            else -> Failure(RemoteFailure(POST, uri, response.status))
        }

        val credentials = Credentials(
            SessionToken.of(""), AccessKeyId.of(""), SecretAccessKey.of(""), Instant.EPOCH)
        val assumeRoleResult = AssumeRoleResult(1, AssumedRoleUser(ARN.of(""), RoleId.of("")), credentials)

        return Success(AssumeRole.Response(AssumeRoleResponse(assumeRoleResult, ResponseMetadata(""))))
    }
}

private fun Response.parse() {
    documentBuilderFactory.parse(body.stream).getElementsByTagName("Key")
    TODO("Not yet implemented")
}
