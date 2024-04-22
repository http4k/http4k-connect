package org.http4k.connect.amazon.sqs.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.sqs.SQSAction
import org.http4k.connect.amazon.sqs.model.SQSMessage
import org.http4k.core.Uri
import se.ansman.kotshi.JsonSerializable
import java.time.Duration
import java.time.ZonedDateTime

@Http4kConnectAction
data class ReceiveMessage(
    val queueUrl: Uri,
    val maxNumberOfMessages: Int? = null,
    val visibilityTimeout: Int? = null,
    val attributeName: String? = null,
    val expires: ZonedDateTime? = null,
    val longPollTime: Duration? = null,
    val messageAttributes: List<String>? = null,
    val receiveRequestAttemptId: String? = null,
    val attributeNames: List<String>? = null,
) : SQSAction<List<SQSMessage>, ReceiveMessageResponse>("ReceiveMessage", ReceiveMessageResponse::class, { it.Messages }) {

    override fun requestBody() = ReceiveMessageData(
        AttributeNames = attributeNames,
        MaxNumberOfMessages = maxNumberOfMessages,
        MessageAttributeNames = messageAttributes,
        QueueUrl = queueUrl,
        ReceiveRequestAttemptId = receiveRequestAttemptId,
        VisibilityTimeout = visibilityTimeout,
        WaitTimeSeconds = longPollTime?.seconds?.toInt()
    )
}

@JsonSerializable
data class ReceiveMessageData(
    val AttributeNames: List<String>? = null,
    val MaxNumberOfMessages: Int? =null,
    val MessageAttributeNames: List<String>? = null,
    val QueueUrl: Uri,
    val ReceiveRequestAttemptId: String? = null,
    val VisibilityTimeout: Int? = null,
    val WaitTimeSeconds: Int? = null
)

@JsonSerializable
data class ReceiveMessageResponse(
    val Messages: List<SQSMessage>
)
