package org.http4k.connect.amazon.sqs.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.sqs.SQSAction
import org.http4k.core.Uri
import se.ansman.kotshi.JsonSerializable
import com.squareup.moshi.Json

@Http4kConnectAction
data class GetQueueAttributes(
    val queueUrl: Uri,
    val attributes: List<String> = listOf("All"),
) : SQSAction<QueueAttributes, QueueAttributes>("GetQueueAttributes", QueueAttributes::class, { it }) {
    override fun requestBody() = GetQueueAttributesData(
        QueueUrl = queueUrl,
        AttributeNames = attributes
    )
}

@JsonSerializable
data class GetQueueAttributesData(
    val QueueUrl: Uri,
    val AttributeNames: List<String>? = null
)

@JsonSerializable
data class QueueAttributes(
    @Json(name = "Attributes") val attributes: Map<String, String>
)
