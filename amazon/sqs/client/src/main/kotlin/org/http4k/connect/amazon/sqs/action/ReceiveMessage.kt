package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.core.children
import org.http4k.connect.amazon.core.firstChildText
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.sequenceOfNodes
import org.http4k.connect.amazon.core.text
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.connect.amazon.sqs.model.QueueName
import org.http4k.connect.amazon.sqs.model.ReceiptHandle
import org.http4k.connect.amazon.sqs.model.SQSMessage
import org.http4k.connect.amazon.sqs.model.SQSMessageId
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
                                    .map { (it.firstChildText("Name") ?: "") to (it.firstChildText("Value") ?:"") }
                                    .toMap()
                            )
                        }.toList()
                })
            else -> Failure(RemoteFailure(POST, uri(), status, bodyString()))
        }
    }

    override fun uri() = Uri.of("/${accountId.value}/${queueName.value}")
}
