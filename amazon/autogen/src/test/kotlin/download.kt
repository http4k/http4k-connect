import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.autogen.ApiJson
import org.http4k.connect.amazon.autogen.AwsServiceApi
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.Jackson.json

fun main() {
    val client = JavaHttpClient()
    val services = client(
        Request(
            GET,
            "https://api.github.com/repos/aws/aws-sdk-java-v2/contents/services"
        )
    ).json<List<AwsService>>()
        .filter { it.isService }
        .map { it.path }
        .filter { it.startsWith("services/") }
        .map { it.removePrefix("services/") }

    services.map { client.retrieveApi(it) }
}

data class AwsService(val path: String, val type: String) {
    val isService = type == "dir"
}

private fun HttpHandler.retrieveApi(name: String): String {
    val response = this(
        Request(
            GET,
            "https://raw.githubusercontent.com/aws/aws-sdk-java-v2/master/services/$name/src/main/resources/codegen-resources/service-2.json"
        )
    )
    return with(ApiJson) {
        when {
            response.status.successful -> try {
                val metadata = response.json<AwsServiceApi>().metadata
                listOf(name, metadata.toString())
            } catch (e: Exception) {
                listOf(name, e.stackTraceToString())
            }

            response.status == Status.NOT_FOUND -> listOf(name, "unique")

            else -> listOf(response.status.toString())
        }.joinToString(", ")
    }
        .also { println(it) }
}
