package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class QueueName private constructor(value: String) : ResourceId(value) {
    companion object : StringValueFactory<QueueName>(::QueueName, 1.minLength)
}

class SQSMessageId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<SQSMessageId>(::SQSMessageId, 1.minLength)
}

class ReceiptHandle private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<ReceiptHandle>(::ReceiptHandle, 1.minLength)
}

data class SQSMessage(
    val messageId: SQSMessageId,
    val body: String,
    val md5OfBody: String,
    val receiptHandle: ReceiptHandle,
    val attributes: Map<String, String>
)
