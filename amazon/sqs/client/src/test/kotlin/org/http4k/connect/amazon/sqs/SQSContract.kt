package org.http4k.connect.amazon.sqs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.QueueName
import org.http4k.connect.amazon.sqs.action.DataType
import org.http4k.connect.amazon.sqs.action.MessageAttribute
import org.http4k.connect.amazon.sqs.action.MessageSystemAttribute
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

abstract class SQSContract(http: HttpHandler) : AwsContract(http) {

    private val sqs by lazy {
        SQS.Http(aws.region, { aws.credentials }, http)
    }

    private val queueName = QueueName.of(UUID.randomUUID().toString())
    private val expires = ZonedDateTime.now().plus(Duration.ofMinutes(1))

    @Test
    fun `queue lifecycle`() {
        with(sqs) {
            val created = createQueue(
                queueName,
                mapOf("tag" to "value"),
                mapOf("MaximumMessageSize" to "10000"),
                expires
            ).successValue()
            val accountId = AwsAccount.of(created.QueueUrl.path.split("/")[1])

            try {
                val id = sendMessage(
                    accountId, queueName, "hello world", expires = expires,
                    attributes = listOf(
                        MessageAttribute("foo", "123", DataType.Number),
                        MessageAttribute("binaryfoo", Base64Blob.encoded("foobar"))
                    ),
                    systemAttributes = listOf(
                        MessageSystemAttribute(
                            "AWSTraceHeader",
                            "Root=1-5f4a4a2d-b94f96db34d41be1349080d2",
                            DataType.String
                        )
                    )
                ).successValue().MessageId

                val received = receiveMessage(accountId, queueName).successValue().first()
                assertThat(received.messageId, equalTo(id))
                assertThat(received.body, equalTo("hello world"))

                deleteMessage(accountId, queueName, received.receiptHandle).successValue()

                assertThat(receiveMessage(accountId, queueName).successValue().size, equalTo(0))
            } finally {
                deleteQueue(accountId, queueName, expires).successValue()
            }
        }
    }
}
