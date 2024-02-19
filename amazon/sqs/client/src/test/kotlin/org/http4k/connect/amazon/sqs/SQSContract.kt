package org.http4k.connect.amazon.sqs

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.present
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.Tag
import org.http4k.connect.amazon.sqs.action.SendMessageBatchEntry
import org.http4k.connect.amazon.sqs.model.MessageAttribute
import org.http4k.connect.amazon.sqs.model.MessageSystemAttribute
import org.http4k.connect.amazon.sqs.model.QueueName
import org.http4k.connect.amazon.sqs.model.ReceiptHandle
import org.http4k.connect.amazon.sqs.model.SQSMessageId
import org.http4k.connect.model.Base64Blob
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

abstract class SQSContract(http: HttpHandler) : AwsContract() {

    val sqs by lazy {
        SQS.Http(aws.region, { aws.credentials }, http)
    }

    val queueName = QueueName.of(UUID.randomUUID().toString())
    val expires = ZonedDateTime.now().plus(Duration.ofMinutes(1))

    @Test
    fun `queue lifecycle`() {
        with(sqs) {
            val created = createQueue(
                queueName,
                listOf(Tag("tag", "value")),
                mapOf("MaximumMessageSize" to "10000"),
                expires
            ).successValue()

            val queueUrl = created.QueueUrl

            try {
                assertThat(
                    getQueueAttributes(
                        queueUrl,
                        listOf("All")
                    ).successValue().attributes["ApproximateNumberOfMessages"], equalTo("0")
                )

                waitABit()

                assertThat(
                    listQueues().successValue().any { it.toString().endsWith(queueUrl.toString()) },
                    equalTo(true)
                )

                val attributes = listOf(
                    MessageAttribute("foo", "123", DataType.Number),
                    MessageAttribute("bar", "123", DataType.Number),
                    MessageAttribute("binaryfoo", Base64Blob.encode("foobar"))
                )
                val id = sendMessage(
                    queueUrl, "hello world", expires = expires,
                    attributes = attributes,
                    systemAttributes = listOf(
                        MessageSystemAttribute(
                            "AWSTraceHeader",
                            "Root=1-5f4a4a2d-b94f96db34d41be1349080d2",
                            DataType.String
                        )
                    )
                ).successValue().MessageId

                val received = receiveMessage(
                    queueUrl,
                    messageAttributes = listOf("foo", "bar", "binaryfoo")
                ).successValue().first()

                assertThat(received.messageId, equalTo(id))
                assertThat(
                    received.attributes.map { Triple(it.name, it.value, it.dataType) }.toSet(), equalTo(
                        setOf(
                            Triple("foo", "123", DataType.Number),
                            Triple("bar", "123", DataType.Number),
                            Triple("binaryfoo", "Zm9vYmFy", DataType.Binary)
                        )
                    )
                )
                assertThat(received.body, equalTo("hello world"))

                deleteMessage(queueUrl, received.receiptHandle).successValue()

                assertThat(receiveMessage(queueUrl).successValue().size, equalTo(0))

                sendMessage(queueUrl, "hello world", expires = expires).successValue()

            } finally {
                deleteQueue(queueUrl, expires).successValue()
            }
        }
    }

    open fun waitABit() {}

    @Test
    fun `batch operations`() {
        val created = sqs.createQueue(queueName, emptyList(), emptyMap()).successValue()
        try {
            val (sent1, sent2, sent3) = sqs.sendMessageBatch(
                queueUrl = created.QueueUrl,
                entries = listOf(
                    SendMessageBatchEntry("msg1", "foo"),
                    SendMessageBatchEntry(
                        "msg2", "bar", attributes = listOf(
                            MessageAttribute("attr1", "123", DataType.Number)
                        )
                    ),
                    SendMessageBatchEntry("msg3", "baz")
                )
            ).successValue()

            assertThat(sent1.Id, equalTo("msg1"))
            assertThat(sent1.MD5OfMessageBody, equalTo("acbd18db4cc2f85cedef654fccc4a4d8"))
            assertThat(sent1.MD5OfMessageAttributes, absent())

            assertThat(sent2.Id, equalTo("msg2"))
            assertThat(sent2.MD5OfMessageBody, equalTo("37b51d194a7513e45b56f6524f2d51f2"))
            assertThat(sent2.MD5OfMessageAttributes, present()) // FIXME fake value disagrees with real AWS

            assertThat(sent3.Id, equalTo("msg3"))
            assertThat(sent3.MD5OfMessageBody, equalTo("73feffa4b7f6bb68e44cf984c85f6e88"))
            assertThat(sent3.MD5OfMessageAttributes, absent())

            val (message1, message2) = sqs.receiveMessage(queueUrl = created.QueueUrl, maxNumberOfMessages = 2)
                .successValue()

            // delete batch
            val result = sqs.deleteMessageBatch(
                queueUrl = created.QueueUrl,
                entries = listOf(
                    message1.messageId to message1.receiptHandle,
                    message2.messageId to message2.receiptHandle,
                    SQSMessageId.of("1337") to ReceiptHandle.of("sdckjdsklfjdsf")
                )
            ).successValue()
            assertThat(result, equalTo(listOf(message1.messageId, message2.messageId)))

            // ensure messages deleted
            assertThat(sqs.receiveMessage(created.QueueUrl).successValue(), hasSize(equalTo(1)))

        } finally {
            sqs.deleteQueue(created.QueueUrl).successValue()
        }
    }
}
