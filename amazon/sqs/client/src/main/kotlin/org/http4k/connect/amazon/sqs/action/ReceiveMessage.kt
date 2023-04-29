package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.core.children
import org.http4k.connect.amazon.core.firstChild
import org.http4k.connect.amazon.core.firstChildText
import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.sequenceOfNodes
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.connect.amazon.sqs.model.MessageAttribute
import org.http4k.connect.amazon.sqs.model.ReceiptHandle
import org.http4k.connect.amazon.sqs.model.SQSMessage
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.connect.asRemoteFailure
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

@Http4kConnectAction
data class ReceiveMessage(
    val queueUrl: Uri,
    val maxNumberOfMessages: Int? = null,
    val visibilityTimeout: Int? = null,
    val attributeName: String? = null,
    val expires: ZonedDateTime? = null,
    val longPollTime: Duration? = null,
    val messageAttributes: List<String>? = null
) : SQSAction<List<SQSMessage>>(
    "ReceiveMessage",
    *(
        (messageAttributes?.mapIndexed { i, n -> "MessageAttributeName.${(i + 1)}" to n } ?: emptyList()) +
            listOfNotNull(
                maxNumberOfMessages?.let { "MaxNumberOfMessages" to it.toString() },
                longPollTime?.let { "WaitTimeSeconds" to it.seconds.toString() },
                visibilityTimeout?.let { "VisibilityTimeout" to it.toString() },
                attributeName?.let { "AttributeName" to it },
                expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) },
                "QueueUrl" to queueUrl.toString()
            )
        ).toTypedArray()
) {

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(
                with(xmlDoc()) {
                    getElementsByTagName("Message")
                        .sequenceOfNodes()
                        .map {
                            SQSMessage(
                                SQSMessageId.of(it.firstChildText("MessageId")!!),
                                it.firstChildText("Body") ?: "",
                                it.firstChildText("MD5OfBody") ?: "",
                                ReceiptHandle.of(it.firstChildText("ReceiptHandle")!!),
                                it.children("MessageAttribute")
                                    .map {
                                        val value = it.firstChild("Value")!!
                                        MessageAttribute(
                                            (it.firstChildText("Name") ?: ""),
                                            (value.firstChildText("StringValue")
                                                ?: value.firstChildText("BinaryValue")
                                                ?: ""),
                                            DataType.valueOf(value.firstChildText("DataType") ?: "")
                                        )
                                    }.toList()
                            )
                        }.toList()
                })
            else -> Failure(asRemoteFailure(this))
        }
    }
}
