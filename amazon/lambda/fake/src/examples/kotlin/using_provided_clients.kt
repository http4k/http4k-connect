import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.lambda.FakeLambda
import org.http4k.connect.amazon.lambda.action.invokeFunction
import org.http4k.connect.amazon.model.FunctionName
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Moshi

data class Req(val value: String)
data class Resp(val value: String)

fun main() {
    val deployedLambda = FunctionName("http4kLambda")

    val fakeLambda = FakeLambda(
        deployedLambda to { req: Request ->
            val request = Moshi.asA<Req>(req.bodyString())
            Response(OK)
                .body(Moshi.asFormatString(Resp(request.value)))
        }
    )

    val client = fakeLambda.client()

    JavaHttpClient()(Request(GET, "http://whatever")).bodyString()
    println(client.invokeFunction<Resp>(deployedLambda, Req("hello"), Moshi))
}

