package org.http4k.connect.amazon.sqs.model

import se.ansman.kotshi.JsonSerializable
import com.squareup.moshi.Json

@JsonSerializable
data class SQSMessage(
    @Json(name = "MessageId") val messageId: SQSMessageId,
    @Json(name = "Body") val body: String,
    @Json(name = "Md5OfBody") val md5OfBody: String,
    @Json(name = "ReceiptHandle") val receiptHandle: ReceiptHandle,
    @Json(name = "Attributes")  val attributes: List<MessageAttribute>
)
