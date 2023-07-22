package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.core.firstChildText
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.connect.amazon.sqs.SQSAction
import org.http4k.connect.amazon.sqs.model.ReceiptHandle
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.connect.asRemoteFailure
import org.http4k.core.Response
import org.http4k.core.Uri

@Http4kConnectAction
data class DeleteMessageBatch(
    val queueUrl: Uri,
    val entries: List<DeleteMessageBatchEntry>,
) : SQSAction<List<SQSMessageId>>(
    "DeleteMessageBatch",
    *entries.withIndex().flatMap { (index, entry) -> entry.toMappings(index + 1) }.toTypedArray(),
    "QueueUrl" to queueUrl.toString()
) {
    override fun toResult(response: Response): Result4k<List<SQSMessageId>, RemoteFailure> = with(response) {
        when {
            status.successful -> {
                val entries = xmlDoc().getElementsByTagName("DeleteMessageBatchResultEntry")
                (0 until entries.length)
                    .map { entries.item(it) }
                    .map { SQSMessageId.of(it.firstChildText("Id")!!) }
                    .let { Success(it) }
            }
            else -> Failure(asRemoteFailure(this))
        }
    }
}

typealias DeleteMessageBatchEntry = Pair<SQSMessageId, ReceiptHandle>

private fun DeleteMessageBatchEntry.toMappings(index: Int) = listOf(
    "DeleteMessageBatchRequestEntry.$index.Id" to first.value,
    "DeleteMessageBatchRequestEntry.$index.ReceiptHandle" to second.value
)
