import org.http4k.aws.AwsSdkClient
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.cloudfront.FakeCloudFront
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import java.util.UUID

const val USE_REAL_CLIENT = true

fun main() {
    val http: HttpHandler = if (USE_REAL_CLIENT) PrintRequestAndResponse().then(JavaHttpClient()) else FakeCloudFront()

    val client = S3Client.builder()
        .region(Region.EU_WEST_2)
        .build()

    val loudClient = S3Client.builder()
        .region(Region.EU_WEST_2)
        .httpClient(AwsSdkClient(http))
        .build()

    val bucketName = UUID.randomUUID().toString().replace('-', '.')

    println(client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build()))

    try {
        loudClient.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build())
    } finally {
        println(client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build()))
    }

}
