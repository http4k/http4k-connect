package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.QueueName
import org.http4k.connect.amazon.model.ReceiptHandle
import org.http4k.connect.amazon.model.SQSMessage
import org.http4k.connect.amazon.model.SQSMessageId
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

@Http4kConnectAction
class ReceiveMessage(
    private val accountId: AwsAccount,
    private val queueName: QueueName,
    maxNumberOfMessages: Int? = null,
    visibilityTimeout: Int? = null,
    attributeName: String? = null,
    expires: ZonedDateTime? = null
) : SQSAction<List<SQSMessage>>(
    "ReceiveMessage",
    maxNumberOfMessages?.let { "MaxNumberOfMessages" to it.toString() },
    visibilityTimeout?.let { "VisibilityTimeout" to it.toString() },
    attributeName?.let { "AttributeName" to it },
    expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) },
) {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(
                with(documentBuilderFactory().parse(response.body.stream)) {
                    getElementsByTagName("Message")
                        .sequenceOfNodes()
                        .map {
                            SQSMessage(
                                SQSMessageId.of(text("MessageId")),
                                text("Body"),
                                text("MD5OfBody"),
                                ReceiptHandle.of(text("ReceiptHandle")),
                                it.children("Attributes")
                                    .map { it.firstChild("Name").textContent to it.firstChild("Value").textContent }
                                    .toMap()
                            )
                        }.toList()
                })
            else -> Failure(RemoteFailure(POST, uri(), status))
        }
    }

    override fun uri() = Uri.of("/${accountId.value}/${queueName.value}")
}
