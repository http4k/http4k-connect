package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.QueueName
import org.http4k.connect.amazon.model.ReceiptHandle
import org.http4k.connect.amazon.model.SQSMessage
import org.http4k.connect.amazon.model.SQSMessageId
import org.http4k.connect.amazon.model.children
import org.http4k.connect.amazon.model.firstChild
import org.http4k.connect.amazon.model.sequenceOfNodes
import org.http4k.connect.amazon.model.text
import org.http4k.connect.amazon.model.xmlDoc
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

@Http4kConnectAction
data class ReceiveMessage(
    val accountId: AwsAccount,
    val queueName: QueueName,
    val maxNumberOfMessages: Int? = null,
    val visibilityTimeout: Int? = null,
    val attributeName: String? = null,
    val expires: ZonedDateTime? = null
) : SQSAction<List<SQSMessage>>(
    "ReceiveMessage",
    maxNumberOfMessages?.let { "MaxNumberOfMessages" to it.toString() },
    visibilityTimeout?.let { "VisibilityTimeout" to it.toString() },
    attributeName?.let { "AttributeName" to it },
    expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) },
) {
    constructor(
        queueARN: ARN,
        maxNumberOfMessages: Int? = null,
        visibilityTimeout: Int? = null,
        attributeName: String? = null,
        expires: ZonedDateTime? = null
    ) : this(
        queueARN.account,
        queueARN.resourceId(QueueName::of),
        maxNumberOfMessages,
        visibilityTimeout,
        attributeName,
        expires
    )

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(
                with(xmlDoc()) {
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
            else -> Failure(RemoteFailure(POST, uri(), status, bodyString()))
        }
    }

    override fun uri() = Uri.of("/${accountId.value}/${queueName.value}")
}
