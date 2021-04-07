package org.http4k.connect.amazon.sqs.model

data class SQSMessage(
    val messageId: SQSMessageId,
    val body: String,
    val md5OfBody: String,
    val receiptHandle: ReceiptHandle,
    val attributes: Map<String, String>
)
