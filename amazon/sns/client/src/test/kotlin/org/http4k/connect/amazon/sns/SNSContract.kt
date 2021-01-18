package org.http4k.connect.amazon.sns

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.DataType.Number
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.TopicName
import org.http4k.connect.amazon.sns.action.MessageAttribute
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
                listOf(Tag("key", "value"), Tag("key2", "value2")),
                mapOf("foo" to "bar")
            ).successValue().topicArn
            try {
                assertThat(listTopics().successValue().contains(topicArn), equalTo(true))

                publishMessage(
                    "hello world", "subject", topicArn = topicArn,
                    attributes = listOf(
                        MessageAttribute("foo", "123", Number),
                        MessageAttribute("bar", "123", Number),
                        MessageAttribute("binaryfoo", Base64Blob.encoded("foobar"))
                    )
                ).successValue()
            } finally {
                deleteTopic(topicArn).successValue()
            }
        }
    }
}
