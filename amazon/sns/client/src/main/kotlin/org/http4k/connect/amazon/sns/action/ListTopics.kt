package org.http4k.connect.amazon.sns.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.Paged
import org.http4k.connect.amazon.PagedAction
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.sequenceOfNodes
import org.http4k.connect.amazon.model.text
import org.http4k.connect.amazon.model.xmlDoc
import org.http4k.core.Method.POST
import org.http4k.core.Response

@Http4kConnectAction
data class ListTopics(val NextToken: String? = null) : SNSAction<TopicList>("ListTopics",
    NextToken?.let { "NextToken" to NextToken }
),
    PagedAction<String, ARN, TopicList, ListTopics> {

    override fun next(token: String) = copy(NextToken = token)

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(
                with(xmlDoc()) {
                    val list = getElementsByTagName("TopicArn")
                        .sequenceOfNodes()
                        .map { ARN.of(it.text()) }
                        .toList()
                    TopicList(list, getElementsByTagName("NextToken").item(0)?.text())
                }
            )
            else -> Failure(RemoteFailure(POST, uri(), status, bodyString()))
        }
    }
}

data class TopicList(
    override val items: List<ARN>,
    val NextToken: String? = null
) : Paged<String, ARN> {
    override fun token() = NextToken
}
