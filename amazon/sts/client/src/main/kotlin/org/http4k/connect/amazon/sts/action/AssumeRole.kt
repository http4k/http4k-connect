package org.http4k.connect.amazon.sts.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AccessKeyId
import org.http4k.connect.amazon.model.AssumedRoleUser
import org.http4k.connect.amazon.model.Credentials
import org.http4k.connect.amazon.model.Expiration
import org.http4k.connect.amazon.model.RoleId
import org.http4k.connect.amazon.model.SecretAccessKey
import org.http4k.connect.amazon.model.SessionToken
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.TokenCode
import org.http4k.core.ContentType.Companion.APPLICATION_FORM_URLENCODED
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE
import org.w3c.dom.Document
import java.time.Duration

@Http4kConnectAction
data class AssumeRole(
    val RoleArn: ARN,
    val RoleSessionName: String,
    val TokenCode: TokenCode? = null,
    val SerialNumber: Long? = null,
    val DurationSeconds: Duration? = null,
    val ExternalId: String? = null,
    val Policy: String? = null,
    val PolicyArns: List<ARN>? = null,
    val Tags: List<Tag>? = null,
    val TransitiveTagKeys: List<String>? = null
) : STSAction<AssumedRole> {

    override fun toRequest(): Request {
        val base = listOf(
            "Action" to "AssumeRole",
            "RoleSessionName" to RoleSessionName,
            "RoleArn" to RoleArn.value,
            "Version" to "2011-06-15"
        )

        val policies = PolicyArns?.mapIndexed { index, next ->
            "PolicyArns.member.${index}.arn" to next.value
        }

        val tags = Tags?.flatMapIndexed { index, next ->
            listOf("Tags.member.${index}.Key" to next.Key, "Tags.member.${index}.Value" to next.Value)
        }

        val transitiveTags = TransitiveTagKeys?.mapIndexed() { index, next ->
            "TransitiveTagKeys.member.${index}" to next
        }

        val other = listOfNotNull(
            ExternalId?.let { "ExternalId" to it },
            Policy?.let { "Policy" to it },
            DurationSeconds?.let { "DurationSeconds" to it.seconds.toString() },
        )

        return listOfNotNull(base, policies, tags, transitiveTags, other)
            .flatten().fold(
                Request(POST, Uri.of(""))
                    .with(CONTENT_TYPE of APPLICATION_FORM_URLENCODED)
            ) { acc, it ->
                acc.form(it.first, it.second)
            }
    }

    override fun toResult(response: Response) = when {
        response.status.successful -> Success(AssumedRole.from(response))
        else -> Failure(RemoteFailure(POST, Uri.of(""), response.status))
    }
}

data class AssumedRole(
    val AssumedRoleUser: AssumedRoleUser,
    val Credentials: Credentials
) {
    companion object {
        fun from(response: Response) =
            with(documentBuilderFactory().parse(response.body.stream)) {
                AssumedRole(
                    AssumedRoleUser(ARN.of(text("Arn")), RoleId.of(text("AssumedRoleId"))),
                    Credentials(
                        SessionToken.of(text("SessionToken")),
                        AccessKeyId.of(text("AccessKeyId")),
                        SecretAccessKey.of(text("SecretAccessKey")),
                        Expiration.parse(text("Expiration"))
                    )
                )
            }
    }
}

private fun Document.text(name: String) = getElementsByTagName(name).item(0).textContent.trim()
