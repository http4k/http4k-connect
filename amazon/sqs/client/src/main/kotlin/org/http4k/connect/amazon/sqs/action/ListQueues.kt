package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.core.sequenceOfNodes
import org.http4k.connect.amazon.core.text
import org.http4k.connect.amazon.core.xmlDoc
import org.http4k.connect.asRemoteFailure
import org.http4k.core.Response
import org.http4k.core.Uri

@Http4kConnectAction
class ListQueues : SQSAction<List<Uri>>("ListQueues") {
    override fun toResult(response: Response) = with(response) {
        val queues = response.xmlDoc()
            .getElementsByTagName("QueueUrl").sequenceOfNodes()
            .map { it.text() }
            .toList()

        when {
            status.successful -> Success(queues.map { Uri.of(it) })
            else -> Failure(asRemoteFailure(this))
        }
    }
}
