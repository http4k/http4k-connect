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

interface Bintray {
    fun uploads(pkg: Package): Result<String, Status>
    fun repo(owner: String): Result<String, Status>
    fun org(owner: String): Result<String, Status>
    fun packageGet(pkg: Package): Result<String, Status>

    companion object
}

data class Downloads(val from: Instant, val to: Instant)

fun Bintray.Companion.Http(credentials: Credentials, httpHandler: HttpHandler = JavaHttpClient()) = object : Bintray {
    private val http = ClientFilters.SetBaseUriFrom(Uri.of("https://api.bintray.com"))
        .then(ClientFilters.BasicAuth(credentials))
        .then(httpHandler)

    override fun uploads(pkg: Package): Result<String, Status> {
        val result = http(Request(POST, "/packages/${pkg.owner}/maven/${pkg.packageName}/stats/time_range_downloads"))
            .with(Body.auto<Downloads>().toLens() of Downloads(Instant.now(), Instant.now()))

        return when {
            result.status.successful -> Success(result.bodyString())
            else -> Failure(result.status)
        }
    }

    override fun repo(owner: String): Result<String, Status> {
        val result = http(Request(GET, "/repos/${owner}/maven"))

        return when {
            result.status.successful -> Success(Jackson.prettify(result.bodyString()))
            else -> Failure(result.status)
        }
    }

    override fun org(owner: String): Result<String, Status> {
        val result = http(Request(GET, "/orgs/${owner}"))

        return when {
            result.status.successful -> Success(Jackson.prettify(result.bodyString()))
            else -> Failure(result.status)
        }
    }

    override fun packageGet(pkg: Package): Result<String, Status> {
        val result = http(Request(GET, "/packages/${pkg.owner}/maven/${pkg.packageName}"))

        return when {
            result.status.successful -> Success(Jackson.prettify(result.bodyString()))
            else -> Failure(result.status)
        }
    }
}
