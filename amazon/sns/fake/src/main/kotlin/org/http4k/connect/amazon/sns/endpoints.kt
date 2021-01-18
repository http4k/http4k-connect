package org.http4k.connect.amazon.sns

import CreateTopicResponse
import DeleteTopicResponse
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.TopicName
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

fun createTopic(topics: Storage<List<SNSMessage>>, awsAccount: AwsAccount) =
    { r: Request -> r.form("Action") == "CreateTopic" }
        .asRouter() bind { req: Request ->
        val topicName = req.form("Name")!!
        if (topics.keySet(topicName).isEmpty()) topics[topicName] = listOf()

        Response(OK).with(
            viewModelLens of CreateTopicResponse(
                ARN.of(SNS.awsService, Region.of("us-east-1"), awsAccount, TopicName.of(topicName))
            )
        )
    }

fun deleteTopic(topics: Storage<List<SNSMessage>>) = { r: Request -> r.form("Action") == "DeleteTopic" }
    .asRouter() bind { req: Request ->
    val topicArn = req.form("TopicARN")!!

    when {
        topics.keySet(topicArn).isEmpty() -> Response(Status.BAD_REQUEST)
        else -> {
            topics.remove(topicArn)
            Response(OK).with(viewModelLens of DeleteTopicResponse)
        }
    }
}

val viewModelLens by lazy {
    Body.viewModel(HandlebarsTemplates().CachingClasspath(), ContentType.APPLICATION_XML).toLens()
}
