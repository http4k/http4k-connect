package org.http4k.connect.amazon.sqs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.model.Tag
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.successValue
import org.junit.jupiter.api.Test
import java.time.Duration

class FakeSQSTest : SQSContract(FakeSQS()) {
    override val aws = fakeAwsEnvironment

    @Test
    fun `multiple messages are handled correctly`() {
        with(sqs) {
            val created = createQueue(
                queueName,
                listOf(Tag("tag", "value")),
                mapOf("MaximumMessageSize" to "10000"),
                expires
            ).successValue()
            val accountId = AwsAccount.of(created.QueueUrl.path.split("/")[1])

            try {
                val id = sendMessage(
                    accountId, queueName, "hello world"
                ).successValue().MessageId
                val id2 = sendMessage(
                    accountId, queueName, "hello world 2"
                ).successValue().MessageId

                val messages = receiveMessage(accountId, queueName,
                    longPollTime = Duration.ofSeconds(10)).successValue()
                assertThat(messages.size, equalTo(2))
                assertThat(messages.first().messageId, equalTo(id))
                assertThat(messages.drop(1).first().messageId, equalTo(id2))
            } finally {
                deleteQueue(accountId, queueName, expires).successValue()
            }
        }
    }

}
