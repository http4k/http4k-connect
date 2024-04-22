package org.http4k.connect.amazon.sqs.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.core.model.Tag
import org.http4k.connect.amazon.sqs.SQSAction
import org.http4k.connect.amazon.sqs.model.QueueName
import org.http4k.core.Uri
import java.time.ZonedDateTime

import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
data class CreateQueue(
    val queueName: QueueName,
    val tags: List<Tag> = emptyList(),
    val attributes: Map<String, String> = emptyMap(),
    val expires: ZonedDateTime? = null
) : SQSAction<CreatedQueue, CreatedQueue>("CreateQueue", CreatedQueue::class, {it}) {

    override fun requestBody() = CreateQueueData(
        QueueName = queueName,
        tags = tags.associate { it.Key to it.Value },
        Attributes = attributes
    )
}

@JsonSerializable
data class CreateQueueData(
    val QueueName: QueueName,
    val tags: Map<String, String>,
    val Attributes: Map<String, String>
)

@JsonSerializable
data class CreatedQueue(
    val QueueUrl: Uri
)
