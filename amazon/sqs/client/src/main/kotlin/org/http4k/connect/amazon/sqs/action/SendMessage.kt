package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.RemoteFailure
import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.lens.Header

data class SendMessage(val payload: String) : SQSAction<SentMessage> {
    override fun toRequest(): Request {
        val base = listOf(
            "Action" to "SendMessage",
            "MessageBody" to payload,
            "Version" to "2012-11-05"
        )

        val listOf = base + listOf<Pair<String, String>>()

        return listOf.fold(Request(Method.POST, uri())
            .with(Header.CONTENT_TYPE of ContentType.APPLICATION_FORM_URLENCODED)) { acc, it ->
            acc.form(it.first, it.second)
        }
    }

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(SentMessage.from(response))
            else -> Failure(RemoteFailure(Method.POST, uri(), status))
        }
    }

    private fun uri() = Uri.of("")
}

data class SentMessage(
    val MD5OfMessageBody: String,
    val MD5OfMessageAttributes: String,
    val MessageId: String
) {
    companion object {
        fun from(response: Response) =
            with(documentBuilderFactory.parse(response.body.stream)) {
                SentMessage(text("MD5OfMessageBody"), text("MD5OfMessageAttributes"), text("MessageId"))
            }
    }
}
