package org.http4k.connect.amazon.sqs.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Uri

@Http4kConnectAction
class ListQueues : SQSAction<List<Uri>>("ListQueues") {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(listOf<Uri>())
            else -> Failure(RemoteFailure(POST, Uri.of(""), status, bodyString()))
        }
    }
}
