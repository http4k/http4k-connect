import org.http4k.aws.AwsSdkClient
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.sqs.FakeSQS
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import software.amazon.awssdk.regions.Region.EU_WEST_2
import software.amazon.awssdk.services.ssooidc.SsoOidcClient
import software.amazon.awssdk.services.ssooidc.model.CreateTokenRequest

const val USE_REAL_CLIENT = false

fun main() {
    val fake = FakeSQS()

    val http =
        if (USE_REAL_CLIENT) PrintRequestAndResponse(debugStream = true).then(JavaHttpClient())
        else {
            PrintRequestAndResponse(debugStream = true).then(fake)
        }

    val sso = SsoOidcClient.builder()
        .region(EU_WEST_2)
        .httpClient(AwsSdkClient(http))
        .build()

    sso.createToken(
        CreateTokenRequest.builder()
            .deviceCode("asd")
            .code("asd")
            .grantType("asd")
            .build())
}
