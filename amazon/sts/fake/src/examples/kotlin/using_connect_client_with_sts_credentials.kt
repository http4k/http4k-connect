import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.sqs.Http
import org.http4k.connect.amazon.sqs.SQS
import org.http4k.connect.amazon.sqs.createQueue
import org.http4k.connect.amazon.sqs.model.QueueName
import org.http4k.connect.amazon.sts.FakeSTS
import org.http4k.connect.amazon.sts.STS
import org.http4k.core.HttpHandler

private const val USE_REAL_CLIENT = false

fun main() {
    val region = Region.of("us-east-1")

    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeSTS()

    // create a client
    val sqs = SQS.Http(region, CredentialsProvider.STS(), http)

    // all operations return a Result monad of the API type
    val result = sqs.createQueue(QueueName.of("foo"), emptyList(), emptyMap())
    println(result)
}
