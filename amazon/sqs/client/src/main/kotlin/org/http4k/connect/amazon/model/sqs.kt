package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

class QueueName private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<QueueName>(::QueueName)
}

class SQSMessageId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<SQSMessageId>(::SQSMessageId)
}

class ReceiptHandle private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ReceiptHandle>(::ReceiptHandle)
}

data class SQSMessage(
    val messageId: SQSMessageId,
    val body: String,
    val md5OfBody: String,
    val receiptHandle: ReceiptHandle,
    val attributes: Map<String, String>
)
