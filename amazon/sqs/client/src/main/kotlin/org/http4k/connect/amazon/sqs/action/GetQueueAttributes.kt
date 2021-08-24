package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.core.firstChildText
import org.http4k.connect.amazon.core.sequenceOfNodes
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Uri

// can be QueueUrl
@Http4kConnectAction
data class GetQueueAttributes(
    val queueUrl: Uri,
    val attributes: List<String> = listOf("All"),
) : SQSAction<QueueAttributes>(
    "GetQueueAttributes",
    *(
        attributes
            .mapIndexed { i, it -> "AttributeName.${i + 1}" to it }
            + ("QueueUrl" to queueUrl.toString())
        ).toTypedArray()
) {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(QueueAttributes.from(response))
            else -> Failure(RemoteFailure(POST, Uri.of(""), status, bodyString()))
        }
    }
}

data class QueueAttributes(
    val attributes: Map<String, String>
) {
    companion object {
        fun from(response: Response) = QueueAttributes(
            response.xmlDoc()
                .getElementsByTagName("Attribute").sequenceOfNodes()
                .map {
                    it.firstChildText("Name")!! to it.firstChildText("Value")!!
                }
                .toMap()
        )
    }
}
