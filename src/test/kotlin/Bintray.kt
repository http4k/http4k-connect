import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.client.JavaHttpClient
import org.http4k.core.Body
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters
import org.http4k.format.Jackson
import org.http4k.format.Jackson.auto
import java.time.Instant

data class Package(val owner: String, val packageName: String)

data class Uploads(val pkg: Package)
data class Repo(val owner: String)
data class Org(val owner: String)

interface Bintray {
    operator fun invoke(request: Uploads): Result<String, Status>
    operator fun invoke(request: Repo): Result<String, Status>
    operator fun invoke(request: Org): Result<String, Status>
    operator fun invoke(request: Package): Result<String, Status>

    companion object
}

data class Downloads(val from: Instant, val to: Instant)

fun Bintray.Companion.Http(credentials: Credentials, httpHandler: HttpHandler = JavaHttpClient()) = object : Bintray {
    private val http = ClientFilters.SetHostFrom(Uri.of("https://api.bintray.com"))
        .then(ClientFilters.BasicAuth(credentials))
        .then(httpHandler)

    override fun invoke(request: Uploads): Result<String, Status> {
        val result = http(
            Request(
                POST,
                "/packages/${request.pkg.owner}/maven/${request.pkg.packageName}/stats/time_range_downloads"
            )
        )
            .with(Body.auto<Downloads>().toLens() of Downloads(Instant.now(), Instant.now()))

        return when {
            result.status.successful -> Success(result.bodyString())
            else -> Failure(result.status)
        }
    }

    override fun invoke(request: Repo): Result<String, Status> {
        val result = http(Request(GET, "/repos/${request.owner}/maven"))

        return when {
            result.status.successful -> Success(Jackson.prettify(result.bodyString()))
            else -> Failure(result.status)
        }
    }

    override fun invoke(request: Org): Result<String, Status> {
        val result = http(Request(GET, "/orgs/${request.owner}"))

        return when {
            result.status.successful -> Success(Jackson.prettify(result.bodyString()))
            else -> Failure(result.status)
        }
    }

    override fun invoke(request: Package): Result<String, Status> {
        val result = http(Request(GET, "/packages/${request.owner}/maven/${request.packageName}"))

        return when {
            result.status.successful -> Success(Jackson.prettify(result.bodyString()))
            else -> Failure(result.status)
        }
    }
}
