package org.http4k.connect.amazon.sqs

import org.http4k.connect.amazon.sqs.action.SentMessageBatchEntry
import org.http4k.connect.amazon.sqs.model.SQSMessage
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.core.Uri
import org.http4k.template.ViewModel
import java.math.BigInteger
import java.security.MessageDigest

data class CreateQueueResponse(val url: Uri) : ViewModel

data class ReceivedMessage(val message: SQSMessage, val md5OfMessageAttributes: String)
data class ReceiveMessageResponse(val messages: List<ReceivedMessage>) : ViewModel

data class SendMessageResponse(
    val message: SQSMessage,
    val messageId: SQSMessageId,
) : ViewModel {
    val body = message.body
    val md5OfMessageBody = message.md5OfBody()
    val md5OfMessageAttributes = message.md5OfAttributes()
}

data class SendMessageBatchResponse(
    val entries: List<SentMessageBatchEntry>
): ViewModel

data class ListQueuesResponse(val queues: List<String>) : ViewModel

data class GetQueueAttributesResponse(val attributes: List<Pair<String, String>>) : ViewModel

object DeleteQueueResponse : ViewModel

object DeleteMessageResponse : ViewModel

data class DeleteMessageBatchResultEntry(val id: SQSMessageId)
data class DeleteMessageBatchResponse(val entries: List<DeleteMessageBatchResultEntry>): ViewModel

fun SQSMessage.md5OfBody() = body.md5()

fun String.md5() = BigInteger(
    1,
    MessageDigest.getInstance("MD5").digest(toByteArray())
).toString(16).padStart(32, '0')

fun SQSMessage.md5OfAttributes() = MessageMD5ChecksumInterceptor.calculateMd5(this.attributes)
