package org.http4k.connect.amazon.sqs

import org.http4k.core.Uri
import org.http4k.template.ViewModel
import java.math.BigInteger
import java.security.MessageDigest
import java.util.UUID

data class CreateQueueResponse(val url: Uri) : ViewModel

data class SendMessageResponse(
    val message: String,
    val messageId: String = UUID.randomUUID().toString()
) : ViewModel {
    val md5OfMessageBody = message.md5()
    val md5OfMessageAttributes = md5OfMessageBody
}

object DeleteQueueResponse : ViewModel

internal fun String.md5() = BigInteger(1,
    MessageDigest.getInstance("MD5").digest(toByteArray())
).toString(16)
