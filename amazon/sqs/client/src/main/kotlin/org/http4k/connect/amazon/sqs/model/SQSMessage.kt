package org.http4k.connect.amazon.sqs.model

import se.ansman.kotshi.JsonSerializable
import com.squareup.moshi.Json

@JsonSerializable
data class SQSMessage(
    @Json(name = "MessageId") val messageId: SQSMessageId,
    @Json(name = "Body") val body: String,
    @Json(name = "MD5OfBody") val md5OfBody: String,
    @Json(name = "ReceiptHandle") val receiptHandle: ReceiptHandle,
    val Attributes: Map<String, SqsMessageAttributeDto>
) {
    constructor(
        messageId: SQSMessageId,
        body: String,
        md5OfBody: String,
        receiptHandle: ReceiptHandle,
        attributes: List<MessageAttribute>
    ): this(
        messageId = messageId,
        body = body,
        md5OfBody = md5OfBody,
        receiptHandle = receiptHandle,
        Attributes = attributes.associate { it.name to it.toDto() }
    )

    val attributes get() = Attributes.map { (name, value) -> value.toInternal(name) }
}
