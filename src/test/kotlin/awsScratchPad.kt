import org.http4k.aws.AwsSdkClient
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.cloudfront.CloudFront
import org.http4k.connect.amazon.cloudfront.FakeCloudFront
import org.http4k.connect.amazon.model.DistributionId
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import software.amazon.awssdk.services.cloudfront.CloudFrontClient
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest
import software.amazon.awssdk.services.cloudfront.model.InvalidationBatch
import software.amazon.awssdk.services.cloudfront.model.Paths

const val USE_REAL_CLIENT = true

fun main() {
    val http: HttpHandler = if (USE_REAL_CLIENT) PrintRequestAndResponse().then(JavaHttpClient()) else FakeCloudFront()

    val distributionId = DistributionId.of("E1HHLORGLBAQYP")
    val client = CloudFrontClient.builder()
        .httpClient(AwsSdkClient(http))
        .build()

    CloudFront


//    println(
//        client.createDistribution(
//            CreateDistributionRequest.builder()
//                .build()
//        )
//    )
//
//    println(
//        client.createDistribution(
//            CreateDistributionRequest.builder()
//                .build()
//        )
//    )

//    CloudFront.Http(
//        Region.of("eu-west-2"),
//        { AwsCredentials("", "") },
//        http
//    ).createInvalidation(DistributionId.of("123"), "foo")
//
    println(
        client.createInvalidation(
            CreateInvalidationRequest.builder()
                .invalidationBatch(
                    InvalidationBatch.builder()
                        .paths(
                            Paths.builder()
                                .items("123", "456")
                                .quantity(5)
                                .build()
                        )
                        .build()
                )
                .distributionId(distributionId.value).build()
        )
    )

}
