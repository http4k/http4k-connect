package org.http4k.connect.amazon.sqs

import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.ReceiptHandle
import org.http4k.connect.amazon.model.SQSMessage
import org.http4k.connect.amazon.model.SQSMessageId
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.extend
import org.http4k.core.with
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.routing.path
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

fun deleteQueue(queues: Storage<List<SQSMessage>>) = { r: Request -> r.form("Action") == "DeleteQueue" }
    .asRouter() bind { req: Request ->
    val queueName = req.path("queueName")!!

    when {
        queues.keySet(queueName).isEmpty() -> Response(Status.BAD_REQUEST)
        else -> {
            queues.remove(queueName)
            Response(OK).with(viewModelLens of DeleteQueueResponse)
        }
    }
}

fun sendMessage(queues: Storage<List<SQSMessage>>) = { r: Request -> r.form("Action") == "SendMessage" }
    .asRouter() bind { req: Request ->
    val queueName = req.path("queueName")!!

    queues[queueName]?.let {
        val message = req.form("MessageBody")!!
        val messageId = SQSMessageId.of(queueName + "/" + UUID.randomUUID())
        val receiptHandle = ReceiptHandle.of(queueName + "/" + UUID.randomUUID())
        queues[queueName] = it + SQSMessage(messageId, message, message.md5(), receiptHandle, mapOf())
        Response(OK).with(viewModelLens of SendMessageResponse(message, messageId))
    } ?: Response(Status.BAD_REQUEST)
}

fun receiveMessage(queues: Storage<List<SQSMessage>>) = { r: Request -> r.form("Action") == "ReceiveMessage" }
    .asRouter() bind { req: Request ->
    val queue = queues[req.path("queueName")!!]
    queue?.let { Response(OK).with(viewModelLens of ReceiveMessageResponse(it)) } ?: Response(Status.BAD_REQUEST)
}

fun deleteMessage(queues: Storage<List<SQSMessage>>) = { r: Request -> r.form("Action") == "DeleteMessage" }
    .asRouter() bind { req: Request ->
    val queueName = req.path("queueName")!!
    val receiptHandle = ReceiptHandle.of(req.form("ReceiptHandle")!!)
    queues[queueName]?.let {
        queues[queueName] = it.filterNot { it.receiptHandle == receiptHandle }
        Response(OK).with(viewModelLens of DeleteMessageResponse)
    } ?: Response(Status.BAD_REQUEST)
}

val viewModelLens by lazy {
    Body.viewModel(HandlebarsTemplates().CachingClasspath(), ContentType.APPLICATION_XML).toLens()
}
