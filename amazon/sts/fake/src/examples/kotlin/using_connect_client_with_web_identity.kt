import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.WebIdentityToken
import org.http4k.connect.amazon.sqs.Http
import org.http4k.connect.amazon.sqs.SQS
import org.http4k.connect.amazon.sqs.createQueue
import org.http4k.connect.amazon.sqs.model.QueueName
import org.http4k.connect.amazon.sts.FakeSTS
import org.http4k.connect.amazon.sts.STSWebIdentity
import org.http4k.core.HttpHandler

private const val USE_REAL_CLIENT = false

fun main() {
    val region = Region.of("us-east-1")
    val roleArn = ARN.of("arn:aws:sts:us-east-1:000000000001:role:myrole")

    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeSTS()

    // create a client
    val sqs = SQS.Http(
        region, CredentialsProvider.STSWebIdentity(
            region,
            roleArn,
            WebIdentityToken.of("foobar")
//            WebIdentityToken.of(File("wit.file")) <-- load token from file or use it directly
        ), http
    )

    // all operations return a Result monad of the API type
    val result = sqs.createQueue(QueueName.of("foo"), emptyList(), emptyMap())
    println(result)
}
