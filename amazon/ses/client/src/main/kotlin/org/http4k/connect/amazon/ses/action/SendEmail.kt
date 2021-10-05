package org.http4k.connect.amazon.ses.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.core.text
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.connect.amazon.ses.model.Destination
import org.http4k.connect.amazon.ses.model.EmailAddress
import org.http4k.connect.amazon.ses.model.HtmlMessage
import org.http4k.connect.amazon.ses.model.Message
import org.http4k.connect.amazon.ses.model.SESMessageId
import org.http4k.connect.amazon.ses.model.TextMessage
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Uri

data class SendEmail(
    val source: EmailAddress,
    val destination: Destination,
    val message: Message,
    val replyToAddresses: Set<EmailAddress>? = null,
) : SESAction<SendEmailResponse>(
    action = "SendEmail",
    "Source" to source.value,
    *destination.toAddresses
        .orEmpty()
        .mapIndexed { idx, it -> "Destination.ToAddresses.member.${idx + 1}" to it.value }
        .toTypedArray(),
    *destination.ccAddresses
        .orEmpty()
        .mapIndexed { idx, it -> "Destination.CcAddresses.member.${idx + 1}" to it.value }
        .toTypedArray(),
    *destination.bccAddresses
        .orEmpty()
        .mapIndexed { idx, it -> "Destination.BccAddresses.member.${idx + 1}" to it.value }
        .toTypedArray(),
    *replyToAddresses
        .orEmpty()
        .mapIndexed { idx, it -> "ReplyToAddresses.member.${idx + 1}" to it.value }
        .toTypedArray(),
    Pair("Message.Subject.Data", message.subject.value),
    when (message.body) {
        is HtmlMessage -> Pair("Message.Body.Html.Data", message.body.value)
        is TextMessage -> Pair("Message.Body.Text.Data", message.body.value)
    }
) {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(SendEmailResponse.from(response))
            else -> Failure(RemoteFailure(POST, Uri.of(""), status, bodyString()))
        }
    }
}

data class SendEmailResponse(val MessageId: SESMessageId) {
    companion object {
        fun from(response: Response) = with(response.xmlDoc()) {
            SendEmailResponse(SESMessageId.of(text("MessageId")))
        }
    }
}
