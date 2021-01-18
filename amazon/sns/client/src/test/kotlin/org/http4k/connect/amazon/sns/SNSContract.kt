package org.http4k.connect.amazon.sns

import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.TopicName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class SNSContract(http: HttpHandler) : AwsContract(http) {
    private val sns by lazy {
        SNS.Http(aws.region, { aws.credentials }, http)
    }
    private val topicName = TopicName.of(UUID.randomUUID().toString())

    @Test
    fun `topic lifecycle`() {
        with(sns) {
            val topicArn = createTopic(
                topicName,
                listOf(Tag("key", "value")),
                mapOf("MaximumMessageSize" to "10000")
            ).successValue().topicArn
            try {
//                val id = sendMessage(
//                    accountId, queueName, "hello world", expires = expires,
//                    attributes = listOf(
//                        MessageAttribute("foo", "123", org.http4k.connect.amazon.model.DataType.Number),
//                        MessageAttribute("bar", "123", org.http4k.connect.amazon.model.DataType.Number),
//                        MessageAttribute("binaryfoo", org.http4k.connect.amazon.model.Base64Blob.encoded("foobar"))
//                    ),
//                    systemAttributes = listOf(
//                        MessageSystemAttribute(
//                            "AWSTraceHeader",
//                            "Root=1-5f4a4a2d-b94f96db34d41be1349080d2",
//                            org.http4k.connect.amazon.model.DataType.String
//                        )
//                    )
//                ).successValue().MessageId
//
//                val received = receiveMessage(accountId, queueName).successValue().first()
//                assertThat(received.messageId, equalTo(id))
//                assertThat(received.body, equalTo("hello world"))
//
//                deleteMessage(accountId, queueName, received.receiptHandle).successValue()
//
//                assertThat(receiveMessage(accountId, queueName).successValue().size, equalTo(0))
            } finally {
                deleteTopic(topicArn).successValue()
            }
        }
    }
}
