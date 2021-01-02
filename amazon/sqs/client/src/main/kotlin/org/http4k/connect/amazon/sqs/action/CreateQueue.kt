package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.QueueName
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

@Http4kConnectAction
class CreateQueue(queueName: QueueName,
                  tags: Map<String, String> = emptyMap(),
                  attributes: Map<String, String> = emptyMap(),
                  expires: ZonedDateTime? = null)
    : SQSAction<SentMessage>(
    "CreateQueue",
    *(tags.entries
        .flatMap { listOf("Tag.Key" to it.key, "Tag.Value" to it.value) } +
        attributes.entries
            .flatMapIndexed { i, it -> listOf("Attribute.${i+1}.Name" to it.key, "Attribute.${i+1}.Value" to it.value) } +
        listOf(
            "QueueName" to queueName.value,
            expires?.let { "Expires" to ISO_ZONED_DATE_TIME.format(it) }
        )
        ).toTypedArray()
) {
    override fun uri() = Uri.of("")

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(SentMessage.from(response))
            else -> Failure(RemoteFailure(POST, uri(), status))
        }
    }
}
