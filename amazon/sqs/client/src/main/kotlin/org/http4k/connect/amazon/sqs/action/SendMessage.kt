package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.QueueName
import org.http4k.connect.amazon.model.SQSMessageId
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

@Http4kConnectAction
class SendMessage(private val accountId: AwsAccount,
                  private val queueName: QueueName,
                  payload: String,
                  delaySeconds: Int? = null,
                  deduplicationId: String? = null,
                  messageGroupId: String? = null,
                  expires: ZonedDateTime? = null,
                  attributes: List<MessageAttribute>? = null,
                  systemAttributes: List<MessageAttribute>? = null
) : SQSAction<SentMessage>(
    "SendMessage",
    *(
        (attributes ?: emptyList())
            .flatMapIndexed { i, it ->
                listOf(
                    "MessageAttribute.${i + 1}.Name" to it.name,
                    "MessageAttribute.${i + 1}.Type" to it.type,
                    "MessageAttribute.${i + 1}.Value" to it.value
                )
            } +
            (systemAttributes ?: emptyList())
                .flatMapIndexed { i, it ->
                    listOf(
                        "MessageSystemAttribute.${i + 1}.Name" to it.name,
                        "MessageSystemAttribute.${i + 1}.Type" to it.type,
                        "MessageSystemAttribute.${i + 1}.Value" to it.value
                    )
                } +
            ("MessageBody" to payload) +
            expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) } +
            delaySeconds?.let { "DelaySeconds" to it.toString() } +
            deduplicationId?.let { "MessageDeduplicationId" to it } +
            messageGroupId?.let { "MessageGroupId" to it }
        ).toTypedArray()
) {

    constructor(queueARN: ARN,
                payload: String,
                delaySeconds: Int? = null,
                deduplicationId: String? = null,
                messageGroupId: String? = null,
                expires: ZonedDateTime? = null,
                attributes: List<MessageAttribute>? = null,
                systemAttributes: List<MessageAttribute>? = null) :
        this(queueARN.account, queueARN.resourceId(QueueName::of), payload, delaySeconds, deduplicationId, messageGroupId, expires, attributes, systemAttributes)

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(SentMessage.from(response))
            else -> Failure(RemoteFailure(Method.POST, uri(), status))
        }
    }

    override fun uri() = Uri.of("/${accountId.value}/${queueName.value}")
}

data class MessageAttribute(val name: String, val value: String, val type: String)

data class SentMessage(
    val MD5OfMessageBody: String,
    val MessageId: SQSMessageId,
    val MD5OfMessageAttributes: String?
) {
    companion object {
        fun from(response: Response) =
            with(documentBuilderFactory().parse(response.body.stream)) {
                SentMessage(text("MD5OfMessageBody"), SQSMessageId.of(text("MessageId")), textOptional("MD5OfMessageAttributes"))
            }
    }
}
