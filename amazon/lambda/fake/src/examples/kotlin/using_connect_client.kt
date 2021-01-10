import dev.forkhandles.result4k.Result
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.lambda.FakeLambda
import org.http4k.connect.amazon.lambda.Http
import org.http4k.connect.amazon.lambda.Lambda
import org.http4k.connect.amazon.lambda.action.invokeFunction
import org.http4k.connect.amazon.model.FunctionName
import org.http4k.connect.amazon.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.debug
import org.http4k.format.Moshi

data class Req(val value: String)
data class Resp(val value: String)

const val USE_REAL_CLIENT = false

fun main() {
    val deployedLambda = FunctionName("http4kLambda")

    val fakeLambda = FakeLambda(
        deployedLambda to { req: Request ->
            val request = Moshi.asA<Req>(req.bodyString())
            Response(OK)
                .body(Moshi.asFormatString(Resp(request.value)))
        }
    )

    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else fakeLambda

    // create a client
    val client = Lambda.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    // all operations return a Result monad of the API type
    val invokeResult: Result<Resp, RemoteFailure> = client.invokeFunction(deployedLambda, Req("hello"), Moshi)
    println(invokeResult)
}

