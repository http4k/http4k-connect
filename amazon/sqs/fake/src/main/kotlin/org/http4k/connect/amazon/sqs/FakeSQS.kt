package org.http4k.connect.amazon.sqs

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.ReceiptHandle
import org.http4k.connect.amazon.model.SQSMessage
import org.http4k.connect.amazon.model.SQSMessageId
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Method.POST
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
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.time.Clock
import java.util.UUID

class FakeSQS(
    private val queues: Storage<List<SQSMessage>> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone(),
    private val awsAccount: AwsAccount = AwsAccount.of("1234567890")
) : ChaosFake() {
    private val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()

    override val app = routes(
        "/{account}/{queueName}" bind POST to routes(
            deleteMessage(),
            deleteQueue(),
            receiveMessage(),
            sendMessage()
        ),
        "/" bind POST to createQueue()
    )

    private fun createQueue() = { r: Request -> r.form("Action") == "CreateQueue" }
        .asRouter() bind { req: Request ->
        val queueName = req.form("QueueName")!!
        if (queues.keySet(queueName).isEmpty()) queues[queueName] = listOf()

        Response(OK).with(lens of CreateQueueResponse(
            req.uri.extend(Uri.of("/$awsAccount/$queueName"))
        ))
    }

    private fun deleteQueue() = { r: Request -> r.form("Action") == "DeleteQueue" }
        .asRouter() bind { req: Request ->
        val queueName = req.path("queueName")!!

        when {
            queues.keySet(queueName).isEmpty() -> Response(BAD_REQUEST)
            else -> Response(OK).with(lens of DeleteQueueResponse)
        }
    }

    private fun sendMessage() = { r: Request -> r.form("Action") == "SendMessage" }
        .asRouter() bind { req: Request ->
        val queueName = req.path("queueName")!!

        queues[queueName]?.let {
            val message = req.form("MessageBody")!!
            val messageId = SQSMessageId.of(queueName + "/" + UUID.randomUUID())
            val receiptHandle = ReceiptHandle.of(queueName + "/" + UUID.randomUUID())
            queues[queueName] = it + SQSMessage(messageId, message, message.md5(), receiptHandle, mapOf())
            Response(OK).with(lens of SendMessageResponse(message, messageId))
        } ?: Response(BAD_REQUEST)
    }

    private fun receiveMessage() = { r: Request -> r.form("Action") == "ReceiveMessage" }
        .asRouter() bind { req: Request ->
        val queue = queues[req.path("queueName")!!]
        queue?.let { Response(OK).with(lens of ReceiveMessageResponse(it)) } ?: Response(BAD_REQUEST)
    }

    private fun deleteMessage() = { r: Request -> r.form("Action") == "DeleteMessage" }
        .asRouter() bind { req: Request ->
        val queueName = req.path("queueName")!!
        val receiptHandle = ReceiptHandle.of(req.form("ReceiptHandle")!!)
        queues[queueName]?.let {
            queues[queueName] = it.filterNot { it.receiptHandle == receiptHandle }
            Response(OK).with(lens of DeleteMessageResponse)
        } ?: Response(BAD_REQUEST)
    }

    /**
     * Convenience function to get a SQS client
     */
    fun client() = SQS.Http(
        AwsCredentialScope("*", "sqs"),
        { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeSQS().start()
}
