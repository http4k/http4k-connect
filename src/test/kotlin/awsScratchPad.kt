import org.http4k.aws.AwsSdkClient
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.cloudfront.FakeCloudFront
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeleteUserPoolRequest

const val USE_REAL_CLIENT = true

fun main() {
    val http: HttpHandler = if (USE_REAL_CLIENT) PrintRequestAndResponse().then(JavaHttpClient()) else FakeCloudFront()

    val loudClient = CognitoIdentityProviderClient.builder()
        .region(Region.EU_WEST_2)
        .httpClient(AwsSdkClient(http))
        .build()

    val id = loudClient.createUserPool(CreateUserPoolRequest.builder().poolName("foobar").build()).userPool().id()

    try {
    } finally {
        println(loudClient.deleteUserPool(DeleteUserPoolRequest.builder().userPoolId(id).build()))
    }

}
