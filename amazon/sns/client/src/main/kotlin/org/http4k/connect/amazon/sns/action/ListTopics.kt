package org.http4k.connect.amazon.sns.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Listing
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.sequenceOfNodes
import org.http4k.connect.amazon.model.text
import org.http4k.connect.amazon.model.xmlDoc
import org.http4k.core.Method.POST
import org.http4k.core.Response

@Http4kConnectAction
class ListTopics : SNSAction<Listing<ARN>>("ListTopics") {
    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(
                with(xmlDoc()) {
                    val list = getElementsByTagName("TopicArn")
                        .sequenceOfNodes()
                        .map { ARN.of(it.text()) }
                        .toList()
                    getElementsByTagName("NextToken").item(0)?.let {
                        Listing.Tokenized(list, it)
                    } ?: Listing.Unpaged(list)
                }
            )
            else -> Failure(RemoteFailure(POST, uri(), status))
        }
    }

    override fun equals(other: Any?) =
        if (this === other) true
        else javaClass == other?.javaClass

    override fun hashCode(): Int = javaClass.hashCode()
}

