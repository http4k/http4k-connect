import org.http4k.client.JavaHttpClient
import org.http4k.config.Environment.Companion.ENV
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.sqs.Http
import org.http4k.connect.amazon.sqs.SQS
import org.http4k.connect.amazon.sqs.createQueue
import org.http4k.connect.amazon.sqs.model.QueueName
import org.http4k.connect.amazon.sts.FakeSTS
import org.http4k.connect.amazon.sts.STSWebIdentity
import org.http4k.core.HttpHandler

private const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeSTS()

    // create a client
    val sqsSimplest = SQS.Http(credentialsProvider = CredentialsProvider.STSWebIdentity())
    // or...
    val sqs = SQS.Http(ENV, http, credentialsProvider = CredentialsProvider.STSWebIdentity())

    // all operations return a Result monad of the API type
    val result = sqs.createQueue(QueueName.of("foo"), emptyList(), emptyMap())
    println(result)
}
