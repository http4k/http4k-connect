package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.core.model.asList
import org.http4k.connect.amazon.core.text
import org.http4k.connect.amazon.core.textOptional
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.connect.amazon.sqs.model.MessageAttribute
import org.http4k.connect.amazon.sqs.model.MessageSystemAttribute
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.connect.toRemoteFailure
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME


// can be QueueUrl
@Http4kConnectAction
data class SendMessage(
    val queueUrl: Uri,
    val payload: String,
    val delaySeconds: Int? = null,
    val deduplicationId: String? = null,
    val messageGroupId: String? = null,
    val expires: ZonedDateTime? = null,
    val attributes: List<MessageAttribute>? = null,
    val systemAttributes: List<MessageSystemAttribute>? = null
) : SQSAction<SentMessage>(
    "SendMessage",
    *(
        asList(attributes ?: emptyList(), systemAttributes ?: emptyList()) +
            listOfNotNull(
                ("MessageBody" to payload),
                expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) },
                "QueueUrl" to queueUrl.toString(),
                delaySeconds?.let { "DelaySeconds" to it.toString() },
                deduplicationId?.let { "MessageDeduplicationId" to it },
                messageGroupId?.let { "MessageGroupId" to it }
            )
        ).toTypedArray()
) {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(SentMessage.from(response))
            else -> Failure(toRemoteFailure(this))
        }
    }
}

data class SentMessage(
    val MD5OfMessageBody: String,
    val MessageId: SQSMessageId,
    val MD5OfMessageAttributes: String? = null
) {
    companion object {
        fun from(response: Response) =
            with(response.xmlDoc()) {
                SentMessage(
                    text("MD5OfMessageBody"),
                    SQSMessageId.of(text("MessageId")),
                    textOptional("MD5OfMessageAttributes")
                )
            }
    }
}
