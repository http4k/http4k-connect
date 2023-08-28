package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.core.firstChildText
import org.http4k.connect.amazon.core.model.asList
import org.http4k.connect.amazon.core.sequenceOfNodes
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.connect.amazon.sqs.SQSAction
import org.http4k.connect.amazon.sqs.model.MessageAttribute
import org.http4k.connect.amazon.sqs.model.MessageSystemAttribute
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.connect.asRemoteFailure
import org.http4k.core.Response
import org.http4k.core.Uri
import org.w3c.dom.Node

@Http4kConnectAction
data class SendMessageBatch(
    val queueUrl: Uri,
    val entries: List<SendMessageBatchEntry>,
) : SQSAction<List<SentMessageBatchEntry>>(
    "SendMessageBatch",
    *entries.flatMapIndexed { index, entry -> entry.toMappings(index + 1) }.toTypedArray(),
    "QueueUrl" to queueUrl.toString()
) {
    override fun toResult(response: Response): Result4k<List<SentMessageBatchEntry>, RemoteFailure> = with(response) {
        when {
            status.successful -> xmlDoc().getElementsByTagName("SendMessageBatchResultEntry")
                .sequenceOfNodes()
                .map { SentMessageBatchEntry.from(it) }
                .let { Success(it.toList()) }
            else -> Failure(asRemoteFailure(this))
        }
    }
}

data class SendMessageBatchEntry(
    val id: String,
    val payload: String,
    val delaySeconds: Int? = null,
    val attributes: List<MessageAttribute>? = null,
    val dedeuplicationId: String? = null,
    val messageGroupId: String? = null,
    val systemAttributes: List<MessageSystemAttribute>? = null,
)

data class SentMessageBatchEntry(
    val Id: String,
    val MessageId: SQSMessageId,
    val MD5OfMessageBody: String? = null,
    val MD5OfMessageAttributes: String? = null,
    val MD5OfMessageSystemAttributes: String? = null
) {
    companion object {
        fun from(node: Node) = SentMessageBatchEntry(
            Id = node.firstChildText("Id")!!,
            MessageId = SQSMessageId.of(node.firstChildText("MessageId")!!),
            MD5OfMessageBody = node.firstChildText("MD5OfMessageBody"),
            MD5OfMessageAttributes = node.firstChildText("MD5OfMessageAttributes"),
            MD5OfMessageSystemAttributes = node.firstChildText("MD5OfMessageSystemAttributes")
        )
    }
}

private fun SendMessageBatchEntry.toMappings(index: Int): List<Pair<String, String>> = buildList {
    add("SendMessageBatchRequestEntry.$index.Id" to id)
    add("SendMessageBatchRequestEntry.$index.MessageBody" to payload)
    if (delaySeconds != null) add("SendMessageBatchRequestEntry.$index.DelaySeconds" to delaySeconds.toString())
    if (attributes != null) addAll(asList(attributes).map { (key, value) -> "SendMessageBatchRequestEntry.$index.$key" to value })
    if (dedeuplicationId != null) add("SendMessageBatchRequestEntry.$index.MessageDeduplicationId" to dedeuplicationId)
    if (messageGroupId != null) add("SendMessageBatchRequestEntry.$index.MessageGroupId" to messageGroupId)
    if (systemAttributes != null) addAll(asList(systemAttributes).map { (key, value) -> "SendMessageBatchRequestEntry.$index.$key" to value })
}
