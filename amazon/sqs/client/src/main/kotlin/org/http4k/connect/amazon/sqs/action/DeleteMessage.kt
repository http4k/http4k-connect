package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.QueueName
import org.http4k.connect.amazon.model.ReceiptHandle
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

@Http4kConnectAction
class DeleteMessage(
    private val accountId: AwsAccount,
    private val queueName: QueueName,
    receiptHandle: ReceiptHandle, expires: ZonedDateTime? = null
) : SQSAction<Unit>(
    "DeleteMessage",
    "ReceiptHandle" to receiptHandle.value,
    expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) }
) {
    constructor(queueARN: ARN, receiptHandle: ReceiptHandle, expires: ZonedDateTime? = null) : this(
        queueARN.account,
        queueARN.resourceId(QueueName::of),
        receiptHandle,
        expires
    )

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(Unit)
            else -> Failure(RemoteFailure(Method.POST, uri(), status))
        }
    }

    override fun uri() = Uri.of("/${accountId.value}/${queueName.value}")
}
