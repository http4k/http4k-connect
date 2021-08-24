package org.http4k.connect.amazon.sqs

import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.sqs.model.ReceiptHandle
import org.http4k.connect.amazon.sqs.model.SQSMessage
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.extend
import org.http4k.core.with
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.util.UUID

fun createQueue(queues: Storage<List<SQSMessage>>, awsAccount: AwsAccount) =
    { r: Request -> r.form("Action") == "CreateQueue" }
        .asRouter() bind { req: Request ->
        val queueName = req.form("QueueName")!!
        if (queues.keySet(queueName).isEmpty()) queues[queueName] = listOf()

        Response(OK).with(
            viewModelLens of CreateQueueResponse(
                req.uri.extend(Uri.of("/$awsAccount/$queueName"))
            )
        )
    }

fun getQueueAttributes(queues: Storage<List<SQSMessage>>) =
    { r: Request -> r.form("Action") == "GetQueueAttributes" }
        .asRouter() bind { req: Request ->
        val queueUri = req.form("QueueUrl")!!

        when (val queue = queues[queueUri.queueName()]) {
            null -> Response(BAD_REQUEST)
            else -> {
                Response(OK).with(
                    viewModelLens of GetQueueAttributesResponse(
                        mapOf(
                            "LastModifiedTimestamp" to "0",
                            "CreatedTimestamp" to "0",
                            "MessageRetentionPeriod" to "0",
                            "DelaySeconds" to "0",
                            "ReceiveMessageWaitTimeSeconds" to "0",
                            "MaximumMessageSize" to "0",
                            "VisibilityTimeout" to "0",
                            "ApproximateNumberOfMessagesDelayed" to queue.size.toString(),
                            "ApproximateNumberOfMessages" to queue.size.toString(),
                            "ApproximateNumberOfMessagesNotVisible" to "0"
                        ).toList()
                    )
                )
            }
        }
    }

private fun String.queueName() = substring(lastIndexOf('/') + 1)

fun deleteQueue(queues: Storage<List<SQSMessage>>) = { r: Request -> r.form("Action") == "DeleteQueue" }
    .asRouter() bind { req: Request ->
    val queueName = req.form("QueueUrl")!!.queueName()

    when {
        queues.keySet(queueName).isEmpty() -> Response(BAD_REQUEST)
        else -> {
            queues.remove(queueName)
            Response(OK).with(viewModelLens of DeleteQueueResponse)
        }
    }
}

fun sendMessage(queues: Storage<List<SQSMessage>>) = { r: Request -> r.form("Action") == "SendMessage" }
    .asRouter() bind { req: Request ->
    val queue = req.form("QueueUrl")!!.queueName()

    queues[queue]?.let {
        val message = req.form("MessageBody")!!
        val messageId = SQSMessageId.of(queue + "/" + UUID.randomUUID())
        val receiptHandle = ReceiptHandle.of(queue + "/" + UUID.randomUUID())
        queues[queue] = it + SQSMessage(messageId, message, message.md5(), receiptHandle, mapOf())
        Response(OK).with(viewModelLens of SendMessageResponse(message, messageId))
    } ?: Response(BAD_REQUEST)
}

fun receiveMessage(queues: Storage<List<SQSMessage>>) = { r: Request -> r.form("Action") == "ReceiveMessage" }
    .asRouter() bind { req: Request ->
    val maxNumberOfMessages = req.form("MaxNumberOfMessages")?.toInt()
    val queue = queues[req.form("QueueUrl")!!.queueName()]
    queue?.let { sqsMessages ->
        val messagesToSend = maxNumberOfMessages?.let { it -> sqsMessages.take(it) } ?: sqsMessages
        Response(OK).with(viewModelLens of ReceiveMessageResponse(messagesToSend))
    } ?: Response(BAD_REQUEST)
}

fun deleteMessage(queues: Storage<List<SQSMessage>>) = { r: Request -> r.form("Action") == "DeleteMessage" }
    .asRouter() bind { req: Request ->
    val queue = req.form("QueueUrl")!!.queueName()
    val receiptHandle = ReceiptHandle.of(req.form("ReceiptHandle")!!)
    queues[queue]
        ?.let {
            queues[queue] = it.filterNot { it.receiptHandle == receiptHandle }
            Response(OK).with(viewModelLens of DeleteMessageResponse)
        }
        ?: Response(BAD_REQUEST)
}

val viewModelLens by lazy {
    Body.viewModel(HandlebarsTemplates().CachingClasspath(), ContentType.APPLICATION_XML).toLens()
}
