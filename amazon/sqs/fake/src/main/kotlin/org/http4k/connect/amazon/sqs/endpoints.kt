package org.http4k.connect.amazon.sqs

import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.AmazonRestfulFake
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.sqs.action.CreateQueueData
import org.http4k.connect.amazon.sqs.action.DeleteMessageBatch
import org.http4k.connect.amazon.sqs.action.DeleteMessageData
import org.http4k.connect.amazon.sqs.action.DeleteQueue
import org.http4k.connect.amazon.sqs.action.GetQueueAttributesData
import org.http4k.connect.amazon.sqs.action.ListQueues
import org.http4k.connect.amazon.sqs.action.ReceiveMessageData
import org.http4k.connect.amazon.sqs.action.SendMessageBatch
import org.http4k.connect.amazon.sqs.action.SendMessageData
import org.http4k.connect.amazon.sqs.action.SentMessageBatchEntry
import org.http4k.connect.amazon.sqs.model.MessageAttribute
import org.http4k.connect.amazon.sqs.model.ReceiptHandle
import org.http4k.connect.amazon.sqs.model.SQSMessage
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.connect.amazon.sqs.model.SqsMessageAttributeDto
import org.http4k.connect.model.Base64Blob
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.extend
import org.http4k.core.with
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.util.UUID

private fun forAction(name: String) = { r: Request -> r.header("X-Amz-Target") == "AmazonSQS.$name" }.asRouter()

fun AmazonRestfulFake.createQueue(queues: Storage<List<SQSMessage>>, awsAccount: AwsAccount) =
    forAction("CreateQueue") bind route<CreateQueueData> { data ->
        if (queues.keySet(data.QueueName.value).isEmpty()) {
            queues[data.QueueName.value] = listOf()
        }

        Response(OK).with(
            viewModelLens of CreateQueueResponse(
                uri.extend(Uri.of("/$awsAccount/${data.QueueName}"))
            )
        ).let { Success(it) }
    }

fun AmazonRestfulFake.getQueueAttributes(queues: Storage<List<SQSMessage>>) =
    forAction("GetQueueAttributes") bind route<GetQueueAttributesData> { data ->
        val queueUri = data.QueueUrl

        when (val queue = queues[queueUri.queueName()]) {
            null -> Success(Response(BAD_REQUEST))
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
                ).let { Success(it) }
            }
        }
    }

fun AmazonRestfulFake.listQueues(region: Region, account: AwsAccount, queues: Storage<List<SQSMessage>>) =
    forAction("ListQueues") bind route<ListQueues> { data ->
        // TODO handle pagination
        Success(Response(OK).with(
            viewModelLens of ListQueuesResponse(
                queues.keySet().map { "https://sqs.${region}.amazonaws.com/${account}/$it" })
        ))
    }

private fun Uri.queueName() = toString().queueName()
private fun String.queueName() = substring(lastIndexOf('/') + 1)

fun AmazonRestfulFake.deleteQueue(queues: Storage<List<SQSMessage>>) =
    forAction("DeleteQueue") bind route<DeleteQueue> { data ->
        val queueName = data.QueueUrl.queueName()

        when {
            queues.keySet(queueName).isEmpty() -> Success(Response(BAD_REQUEST))
            else -> {
                queues.remove(queueName)
                Success(Response(OK).with(viewModelLens of DeleteQueueResponse))
            }
        }
    }

fun AmazonRestfulFake.sendMessage(queues: Storage<List<SQSMessage>>) =
    forAction("SendMessage") bind route<SendMessageData> { data ->
        val queue = data.QueueUrl.queueName()

        queues[queue]?.let {
            val messageId = SQSMessageId.of(UUID.randomUUID().toString())
            val receiptHandle = ReceiptHandle.of(UUID.randomUUID().toString())

            val sqsMessage = SQSMessage(messageId, data.MessageBody, data.MessageBody.md5(), receiptHandle, data.MessageAttributes.orEmpty().map { it.value.toInternal(it.key) })
            queues[queue] = it + sqsMessage
            Success(Response(OK).with(viewModelLens of SendMessageResponse(sqsMessage, messageId)))
        } ?: Success(Response(BAD_REQUEST).body("Queue named $queue not found"))
    }

fun AmazonRestfulFake.sendMessageBatch(queues: Storage<List<SQSMessage>>) =
    forAction("SendMessageBatch") bind route<SendMessageBatch> fn@{ data ->
        val queueName = data.queueUrl.queueName()
        val queue = queues[queueName] ?: return@fn Success(Response(BAD_REQUEST).body("Queue named $queueName not found"))

        val results = data.entries.map { entry ->
            val message = SQSMessage(
                messageId = SQSMessageId.of(UUID.randomUUID().toString()),
                body = entry.MessageBody,
                md5OfBody = entry.MessageBody.md5(),
                receiptHandle = ReceiptHandle.of(UUID.randomUUID().toString()),
                attributes = entry.MessageAttributes.orEmpty().map { it.value.toInternal(it.key) }
            )

            val result = SentMessageBatchEntry(
                Id = entry.Id,
                MessageId = message.messageId,
                MD5OfMessageBody = message.md5OfBody(),
                MD5OfMessageAttributes = if (message.attributes.isNotEmpty()) message.md5OfAttributes() else null
            )

            message to result
        }

        queues[queueName] = queue + results.map { it.first }

        Success(Response(OK).with(viewModelLens of SendMessageBatchResponse(results.map { it.second })))
    }

fun AmazonRestfulFake.receiveMessage(queues: Storage<List<SQSMessage>>) =
    forAction("ReceiveMessage") bind route<ReceiveMessageData> { data ->
        val maxNumberOfMessages = data.MaxNumberOfMessages
        val queue = data.QueueUrl.queueName()
        queues[queue]?.let { sqsMessages ->
            val messagesToSend = maxNumberOfMessages?.let { sqsMessages.take(it) } ?: sqsMessages
            Success(Response(OK).with(viewModelLens of ReceiveMessageResponse(
                messagesToSend.map { ReceivedMessage(it, it.md5OfAttributes()) }
            )))
        } ?: Success(Response(BAD_REQUEST).body("Queue named $queue not found"))
    }

fun AmazonRestfulFake.deleteMessage(queues: Storage<List<SQSMessage>>) =
    forAction("DeleteMessage") bind route<DeleteMessageData> { data ->
        val queue = data.QueueUrl.queueName()
        val receiptHandle = data.ReceiptHandle
        queues[queue]
            ?.let {
                queues[queue] = it.filterNot { it.receiptHandle == receiptHandle }
                Success(Response(OK).with(viewModelLens of DeleteMessageResponse))
            }
            ?: Success(Response(BAD_REQUEST).body("Queue named $queue not found"))
    }

fun AmazonRestfulFake.deleteMessageBatch(queues: Storage<List<SQSMessage>>) =
    forAction("DeleteMessageBatch") bind route<DeleteMessageBatch> fn@{ data ->
        val queueName = data.queueUrl.queueName()
        val queue = queues[queueName] ?: return@fn Success(Response(BAD_REQUEST).body("Queue named $queueName not found"))

        val toDelete = data.entries.mapNotNull { entry ->
            queue.find { it.receiptHandle == entry.ReceiptHandle }
        }.toSet()

        queues[queueName] = queue - toDelete

        val result = DeleteMessageBatchResponse(
            entries = data.entries
                .filter { entry -> entry.ReceiptHandle in toDelete.map { it.receiptHandle } }
                .map { DeleteMessageBatchResultEntry(it.Id) }
        )
        Success(Response(OK).with(viewModelLens of result))
    }

val viewModelLens by lazy {
    Body.viewModel(HandlebarsTemplates().CachingClasspath(), ContentType.APPLICATION_XML).toLens()
}

// TODO fixme
private fun SqsMessageAttributeDto.toInternal(name: String) = when(DataType) {
    org.http4k.connect.amazon.core.model.DataType.String -> MessageAttribute(name, StringValue!!, DataType)
    org.http4k.connect.amazon.core.model.DataType.Binary -> MessageAttribute(name, Base64Blob.of(BinaryValue!!))
    org.http4k.connect.amazon.core.model.DataType.Number -> MessageAttribute(name, StringValue!!, DataType)
}
