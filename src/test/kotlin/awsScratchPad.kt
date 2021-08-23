import org.http4k.aws.AwsSdkClient
import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName

const val USE_REAL_CLIENT = true

fun main() {
    val http: HttpHandler =
        if (USE_REAL_CLIENT) PrintRequestAndResponse(debugStream = true).then(JavaHttpClient()) else PrintRequestAndResponse().then {
            Response(
                OK
            )
        }

    val sqs = SqsClient.builder()
        .region(Region.EU_WEST_2)
        .httpClient(AwsSdkClient(http))
        .build()

    sqs.getQueueAttributes(
        GetQueueAttributesRequest
            .builder()
            .attributeNames(QueueAttributeName.ALL)
            .queueUrl("foo")
            .build()
    )
}
