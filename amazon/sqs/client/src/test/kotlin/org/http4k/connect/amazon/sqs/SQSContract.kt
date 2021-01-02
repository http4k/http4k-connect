package org.http4k.connect.amazon.sqs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.QueueName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

abstract class SQSContract(http: HttpHandler) : AwsContract(AwsService.of("sqs"), http) {

    private val sqs by lazy {
        SQS.Http(aws.scope, { aws.credentials }, http)
    }

    private val queueName = QueueName.of(UUID.randomUUID().toString())
    private val expires = ZonedDateTime.now().plus(Duration.ofMinutes(1))

    @Test
    fun `queue lifecycle`() {
        with(sqs) {
            val created = createQueue(queueName,
                mapOf("tag" to "value"),
                mapOf("MaximumMessageSize" to "10000"),
                expires).successValue()
            val accountId = AwsAccount.of(created.QueueUrl.path.split("/")[1])

            try {
                val id = sendMessage(accountId, queueName, "hello world", expires)
                    .successValue().MessageId

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
