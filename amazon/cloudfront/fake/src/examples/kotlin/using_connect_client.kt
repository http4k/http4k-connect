import dev.forkhandles.result4k.Result
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.cloudfront.CloudFront
import org.http4k.connect.amazon.cloudfront.FakeCloudFront
import org.http4k.connect.amazon.cloudfront.Http
import org.http4k.connect.amazon.cloudfront.createInvalidation
import org.http4k.connect.amazon.model.CallerReference
import org.http4k.connect.amazon.model.DistributionId
import org.http4k.core.HttpHandler
import org.http4k.filter.debug

const val USE_REAL_CLIENT = true

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeCloudFront()

    // create a client
    val client =
        CloudFront.Http({ AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    val distId = DistributionId.of("a-distribution-id")

    // all operations return a Result monad of the API type
    val r: Result<Unit, RemoteFailure> = client
        .createInvalidation(distId, listOf("/path"), 1, CallerReference.random())
//    val createdSecretResult: Result<CreatedSecret, RemoteFailure> =
//        client.createSecret(secretId.value, UUID.randomUUID(), "value")
//    println(createdSecretResult.valueOrNull())
//
//    // get the secret value back
//    println(client.getSecretValue(secretId).valueOrNull())
}
