package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.QueueName
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

@Http4kConnectAction
class DeleteQueue(private val accountId: AwsAccount,
                  private val queueName: QueueName,
                  expires: ZonedDateTime? = null)
    : SQSAction<SentMessage>(
    "DeleteQueue",
    expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) }

) {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(SentMessage.from(response))
            else -> Failure(RemoteFailure(POST, uri(), status))
        }
    }

    override fun uri() = Uri.of("/${accountId.value}/${queueName.value}")
}
