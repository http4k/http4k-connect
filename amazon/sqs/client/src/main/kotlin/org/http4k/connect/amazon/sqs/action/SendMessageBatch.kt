package org.http4k.connect.amazon.sqs.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.sqs.SQSAction
import org.http4k.connect.amazon.sqs.model.BatchResultErrorEntry
import org.http4k.connect.amazon.sqs.model.MessageAttribute
import org.http4k.connect.amazon.sqs.model.MessageSystemAttribute
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.connect.amazon.sqs.model.SqsMessageAttributeDto
import org.http4k.connect.amazon.sqs.model.toDto
import org.http4k.core.Uri
import se.ansman.kotshi.JsonSerializable
import com.squareup.moshi.Json

@JsonSerializable
@Http4kConnectAction
data class SendMessageBatch(
    @Json(name = "QueueUrl") val queueUrl: Uri,
    @Json(name = "Entries") val entries: List<SendMessageBatchEntry>,
) : SQSAction<List<SendMessageBatchResultEntry>, SendMessageBatchResponse>("SendMessageBatch", SendMessageBatchResponse::class, { it.Successful }) {

    override fun requestBody() = this
}

@JsonSerializable
data class SendMessageBatchEntry(
    val Id: String,
    val MessageBody: String,
    val DelaySeconds: Int?,
    val MessageAttributes: Map<String, SqsMessageAttributeDto>?,
    val MessageDedeuplicationId: String?,
    val MessageGroupId: String?,
    val MessageSystemAttributes: Map<String, SqsMessageAttributeDto>?,
) {
    constructor(
        id: String,
        payload: String,
        delaySeconds: Int? = null,
        attributes: List<MessageAttribute>? = null,
        deduplicationId: String? = null,
        messageGroupId: String? = null,
        messageSystemAttributes: List<MessageSystemAttribute>? = null
    ): this(
        Id = id,
        MessageBody = payload,
        DelaySeconds = delaySeconds,
        MessageAttributes = attributes?.associate { it.name to it.toDto() },
        MessageDedeuplicationId = deduplicationId,
        MessageGroupId = messageGroupId,
        MessageSystemAttributes = messageSystemAttributes?.associate { it.name to it.toDto() }
    )
}

@JsonSerializable
data class SentMessageBatchEntry(
    val Id: String,
    val MessageId: SQSMessageId,
    val MD5OfMessageBody: String? = null,
    val MD5OfMessageAttributes: String? = null,
    val MD5OfMessageSystemAttributes: String? = null
)

@JsonSerializable
data class SendMessageBatchResponse(
    val Failed: List<BatchResultErrorEntry>,
    val Successful: List<SendMessageBatchResultEntry>
)

@JsonSerializable
data class SendMessageBatchResultEntry(
    val Id: String,
    val MD5OfMessageBody: String,
    val MessageId: SQSMessageId,
    val MD5OfMessageAttributes: String? = null,
    val SequenceNumber: String? = null
)
