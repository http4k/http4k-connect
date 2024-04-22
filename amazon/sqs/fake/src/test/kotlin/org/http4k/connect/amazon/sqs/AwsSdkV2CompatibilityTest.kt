package org.http4k.connect.amazon.sqs

import org.http4k.aws.AwsSdkClient
import org.http4k.core.then
import org.http4k.filter.RequestFilters
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry

class AwsSdkV2CompatibilityTest {

    private val fake = RequestFilters.Tap {
        println(it)
    }.then(FakeSQS())

    private val client = SqsClient.builder()
        .httpClient(AwsSdkClient(fake))
        .credentialsProvider { AwsBasicCredentials.create("id", "secret") }
        .region(Region.CA_CENTRAL_1)
        .build()

    @Test
    fun `round trip`() {
        val url = client.createQueue {
            it.queueName("foo")
        }.queueUrl()

        client.listQueues()

        client.sendMessage {
            it.queueUrl(url)
            it.messageBody("bar")
        }

        val message1 = client.receiveMessage {
            it.queueUrl(url)
        }.messages().first()

        client.deleteMessage {
            it.queueUrl(url)
            it.receiptHandle(message1.receiptHandle())
        }

        client.sendMessageBatch {
            it.queueUrl(url)
            it.entries(
                SendMessageBatchRequestEntry.builder().id("1").messageBody("hi").build()
            )
        }

        val message2 = client.receiveMessage {
            it.queueUrl(url)
        }.messages().first()

        client.deleteMessageBatch {
            it.queueUrl(url)
            it.entries(
                DeleteMessageBatchRequestEntry.builder().id("1").receiptHandle(message2.receiptHandle()).build()
            )
        }

        client.deleteQueue {
            it.queueUrl(url)
        }
    }
}
