import org.http4k.aws.AwsSdkClient
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.sqs.FakeSQS
import org.http4k.connect.amazon.sqs.createQueue
import org.http4k.connect.amazon.sqs.model.QueueName
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import software.amazon.awssdk.regions.Region.EU_WEST_2
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

const val USE_REAL_CLIENT = false

fun main() {
    val fake = FakeSQS()

    fake.client().createQueue(QueueName.of("myqueue"), emptyList(), emptyMap())

    val http =
        if (USE_REAL_CLIENT) PrintRequestAndResponse(debugStream = true).then(JavaHttpClient())
        else {
            PrintRequestAndResponse(debugStream = true).then(fake)
        }

    val sqs = SqsClient.builder()
        .region(EU_WEST_2)
        .httpClient(AwsSdkClient(http))
        .build()

    sqs.sendMessage(
        SendMessageRequest.builder().messageBody("helloworld")
            .queueUrl("https://foobar/123/myqueue")
            .build()
    )

    sqs.receiveMessage(
        ReceiveMessageRequest.builder()
            .queueUrl("https://foobar/123/myqueue").build()
    )

    sqs.sendMessage(
        SendMessageRequest.builder().messageBody("helloworld")
            .queueUrl("https://foobar/123/myqueue")
            .messageAttributes(
                mapOf(
                    "attr" to
                        MessageAttributeValue.builder()
                            .stringValue("foobar")
                            .dataType("String")
                            .build()
                )
            )
            .build()
    )

    sqs.receiveMessage(
        ReceiveMessageRequest.builder()
            .queueUrl("https://foobar/123/myqueue")
            .messageAttributeNames("attr").build()
    )
}
