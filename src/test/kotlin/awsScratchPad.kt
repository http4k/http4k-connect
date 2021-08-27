import org.http4k.aws.AwsSdkClient
import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest

const val USE_REAL_CLIENT = true

fun main() {
    val http: HttpHandler =
        if (USE_REAL_CLIENT) PrintRequestAndResponse(debugStream = true).then(JavaHttpClient()) else PrintRequestAndResponse().then {
            Response(
                OK
            )
        }

    val sqs = StsClient.builder()
        .region(Region.EU_WEST_2)
        .httpClient(AwsSdkClient(http))
        .build()

    sqs.assumeRoleWithWebIdentity(
        AssumeRoleWithWebIdentityRequest.builder()
            .roleArn("arn:aws:iam::123456789012:role/aws-service-role/access-analyzer.amazonaws.com/AWSServiceRoleForAccessAnalyzer")
            .roleSessionName("testAR")
            .webIdentityToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .build()
    )
}
