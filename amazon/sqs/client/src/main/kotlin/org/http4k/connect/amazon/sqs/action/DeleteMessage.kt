package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.sqs.model.ReceiptHandle
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

@Http4kConnectAction
data class DeleteMessage(
    val queueUrl: Uri,
    val receiptHandle: ReceiptHandle,
    val expires: ZonedDateTime? = null
) : SQSAction<Unit>(
    "DeleteMessage",
    "ReceiptHandle" to receiptHandle.value,
    expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) },
    "QueueUrl" to queueUrl.toString()
) {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(Unit)
            else -> Failure(RemoteFailure(Method.POST, Uri.of(""), status, bodyString()))
        }
    }
}
