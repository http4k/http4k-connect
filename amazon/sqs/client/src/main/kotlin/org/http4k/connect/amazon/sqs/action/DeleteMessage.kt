package org.http4k.connect.amazon.sqs.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.sqs.SQSAction
import org.http4k.connect.amazon.sqs.model.ReceiptHandle
import org.http4k.core.Uri
import java.time.ZonedDateTime

import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
data class DeleteMessage(
    val queueUrl: Uri,
    val receiptHandle: ReceiptHandle,
    val expires: ZonedDateTime? = null
) : SQSAction<Unit, Unit>("DeleteMessage", Unit::class, { }) {

    override fun requestBody() = DeleteMessageData(
        QueueUrl = queueUrl,
        ReceiptHandle = receiptHandle
    )
}

@JsonSerializable
data class DeleteMessageData(
    val QueueUrl: Uri,
    val ReceiptHandle: ReceiptHandle
)
