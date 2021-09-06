package org.http4k.connect.amazon.sqs

import org.http4k.connect.amazon.sqs.model.SQSMessage
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.core.Uri
import org.http4k.template.ViewModel
import java.security.MessageDigest

data class CreateQueueResponse(val url: Uri) : ViewModel

data class ReceiveMessageResponse(val messages: List<SQSMessage>) : ViewModel

data class SendMessageResponse(
    val body: String,
    val messageId: SQSMessageId
) : ViewModel {
    val md5OfMessageBody = body.md5()
    val md5OfMessageAttributes = md5OfMessageBody
}

data class GetQueueAttributesResponse(val attributes: List<Pair<String, String>>) : ViewModel

object DeleteQueueResponse : ViewModel

object DeleteMessageResponse : ViewModel

internal fun String.md5() = BigIntegHer(
    1,
    MessageDigest.getInstance("MD5").digest(toByteArray())
).toString(16).padStart(32, '0')
