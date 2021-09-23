package org.http4k.connect.amazon.sqs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.Tag
import org.http4k.connect.amazon.sqs.model.MessageAttribute
import org.http4k.connect.amazon.sqs.model.MessageSystemAttribute
import org.http4k.connect.amazon.sqs.model.QueueName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.filter.debug
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

abstract class SQSContract(http: HttpHandler) : AwsContract() {

    protected val sqs by lazy {
        SQS.Http(aws.region, { aws.credentials }, http.debug())
    }

    protected val queueName = QueueName.of(UUID.randomUUID().toString())
    protected val expires = ZonedDateTime.now().plus(Duration.ofMinutes(1))

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

                assertThat(
                    listQueues().successValue(), hasElement(queueUrl)
                )

                val id = sendMessage(
                    queueUrl, "hello world", expires = expires,
                    attributes = listOf(
                        MessageAttribute("foo", "123", DataType.Number),
                        MessageAttribute("bar", "123", DataType.Number),
                        MessageAttribute("binaryfoo", Base64Blob.encode("foobar"))
                    ),
                    systemAttributes = listOf(
                        MessageSystemAttribute(
                            "AWSTraceHeader",
                            "Root=1-5f4a4a2d-b94f96db34d41be1349080d2",
                            DataType.String
                        )
                    )
                ).successValue().MessageId

                val received = receiveMessage(queueUrl).successValue().first()
                assertThat(received.messageId, equalTo(id))
                assertThat(received.body, equalTo("hello world"))

                deleteMessage(queueUrl, received.receiptHandle).successValue()

                assertThat(receiveMessage(queueUrl).successValue().size, equalTo(0))
            } finally {
                deleteQueue(queueUrl, expires).successValue()
            }
        }
    }
}
