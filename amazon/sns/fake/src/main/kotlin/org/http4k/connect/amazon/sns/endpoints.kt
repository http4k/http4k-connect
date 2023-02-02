package org.http4k.connect.amazon.sns

import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.sns.model.MessageAttribute
import org.http4k.connect.amazon.sns.model.SNSMessageId
import org.http4k.connect.amazon.sns.model.TopicName
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.core.body.formAsMap
import org.http4k.core.with
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.util.*

fun createTopic(topics: Storage<List<SNSMessage>>, awsAccount: AwsAccount, region: Region) =
    { r: Request -> r.form("Action") == "CreateTopic" }
        .asRouter() bind { req: Request ->
        val topicName = TopicName.of(req.form("Name")!!)
        if (topics.keySet(topicName.value).isEmpty()) topics[topicName.value] = listOf()

        Response(OK).with(
            viewModelLens of CreateTopicResponse(
                ARN.of(SNS.awsService, region, awsAccount, topicName)
            )
        )
    }

fun deleteTopic(topics: Storage<List<SNSMessage>>, awsAccount: AwsAccount, region: Region) = { r: Request -> r.form("Action") == "DeleteTopic" }
    .asRouter() bind { req: Request ->
    val topicName = ARN.parse(req.form("TopicArn")!!).resourceId(TopicName::of)

    when {
        topics.keySet(topicName.value).isEmpty() -> Response(BAD_REQUEST).body("cannot find topic $topicName in $region/$awsAccount. Existing: ${topics.keySet()}")
        else -> {
            topics.remove(topicName.value)
            Response(OK).with(viewModelLens of DeleteTopicResponse)
        }
    }
}

fun listTopics(topics: Storage<List<SNSMessage>>, awsAccount: AwsAccount, region: Region) =
    { r: Request -> r.form("Action") == "ListTopics" }
        .asRouter() bind {
        Response(OK).with(
            viewModelLens of ListTopicsResponse(
                topics.keySet("").map { ARN.of(SNS.awsService, region, awsAccount, it) })
        )
    }

fun publish(topics: Storage<List<SNSMessage>>, awsAccount: AwsAccount, region: Region) = { r: Request -> r.form("Action") == "Publish" }
    .asRouter() bind { req: Request ->

    val topicName = ARN.parse(req.form("TopicArn")!!).resourceId(TopicName::of)

    when {
        topics.keySet(topicName.value).isEmpty() -> Response(BAD_REQUEST).body("cannot find topic $topicName in $region/$awsAccount. Existing: ${topics.keySet()}")
        else -> {
            topics[topicName.value] = topics[topicName.value]!! + SNSMessage(req.form("Message")!!, req.form("Subject"), attributesFrom(req))
            Response(OK).with(viewModelLens of PublishResponse(SNSMessageId.of(UUID.randomUUID().toString())))
        }
    }
}

val viewModelLens by lazy {
    Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()
}

private fun attributesFrom(req: Request): List<MessageAttribute> {
    val names = req.formAsMap()
        .filter { it.key.startsWith("MessageAttributes") }
        .filter { it.key.endsWith(".Name") }
        .map {
            it.value.first()!!.removePrefix("[").removeSuffix("]") to
                it.key.removePrefix("MessageAttributes.entry.").removeSuffix(".Name")
        }

    val cleanedValues = req.formAsMap().mapKeys {
        it.key
            .removeSuffix(".StringValue")
            .removeSuffix(".BinaryValue")
    }

    return names.map {
        MessageAttribute(
            it.first,
            cleanedValues["MessageAttributes.entry.${it.second}.Value"]
            !!.toString().removePrefix("[").removeSuffix("]"),
            DataType.valueOf(cleanedValues["MessageAttributes.entry.${it.second}.Value.DataType"]!![0]!!)
        )
    }
}
