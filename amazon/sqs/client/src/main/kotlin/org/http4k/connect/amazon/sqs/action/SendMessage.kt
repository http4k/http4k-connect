package org.http4k.connect.amazon.sqs.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.MessageFields
import org.http4k.connect.amazon.sqs.SQSAction
import org.http4k.connect.amazon.sqs.model.MessageAttribute
import org.http4k.connect.amazon.sqs.model.MessageSystemAttribute
import org.http4k.connect.amazon.sqs.model.SQSMessageAttribute
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.connect.amazon.sqs.model.SqsMessageAttributeDto
import org.http4k.connect.amazon.sqs.model.toDto
import org.http4k.core.Uri
import se.ansman.kotshi.JsonSerializable
import java.time.ZonedDateTime

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
) : SQSAction<SentMessage, SentMessage>("SendMessage", SentMessage::class, { it} ) {
    override fun requestBody() = SendMessageData(
        DelaySeconds = delaySeconds,
        MessageAttributes = attributes?.associate { it.name to it.toDto() },
        MessageBody = payload,
        MessageDeDuplicationId = deduplicationId,
        MessageGroupId = messageGroupId,
        MessageSystemAttributes = systemAttributes?.associate { it.name to it.toDto() },
        QueueUrl = queueUrl
    )
}

@JsonSerializable
data class SendMessageData(
    val DelaySeconds: Int? = null,
    val MessageAttributes: Map<String, SqsMessageAttributeDto>? = null,
    val MessageBody: String,
    val MessageDeDuplicationId: String? = null,
    val MessageGroupId: String? = null,
    val MessageSystemAttributes: Map<String, SqsMessageAttributeDto>? = null,
    val QueueUrl: Uri
)

@JsonSerializable
data class SentMessage(
    val MD5OfMessageBody: String,
    val MessageId: SQSMessageId,
    val MD5OfMessageAttributes: String? = null,
    val SequenceNumber: String? = null
)
