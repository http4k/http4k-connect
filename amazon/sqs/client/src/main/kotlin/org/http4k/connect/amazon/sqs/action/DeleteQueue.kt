package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.sqs.model.QueueName
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

// can be QueueUrl
@Http4kConnectAction
data class DeleteQueue(
    val accountId: AwsAccount,
    val queueName: QueueName,
    val expires: ZonedDateTime? = null
) : SQSAction<Unit>(
    "DeleteQueue",
    expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) }
) {
    constructor(queueARN: ARN, expires: ZonedDateTime? = null) : this(
        queueARN.account,
        queueARN.resourceId(QueueName::of),
        expires
    )

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(Unit)
            else -> Failure(RemoteFailure(POST, uri(), status, bodyString()))
        }
    }

    override fun uri() = Uri.of("/${accountId.value}/${queueName.value}")
}
