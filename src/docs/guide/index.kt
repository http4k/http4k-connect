package guide

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.map
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom




    interface APIAction<R> {
        fun toRequest(): Request
        fun toResult(response: Response): Result<R, RemoteFailure>
    }



    data class Reverse(val value: String) : APIAction<String> {
        override fun toRequest() = Request(POST, "/reverse").body(value)

        override fun toResult(response: Response): Result<String, RemoteFailure> =
            Success(response.bodyString())
    }

    fun API.reverse(value: String) = this(Reverse(value))


    class API(rawHttp: HttpHandler) {
        private val transport = SetBaseUriFrom(Uri.of("https://api.com"))
            .then(rawHttp)

        operator fun <R> invoke(action: APIAction<R>): Result<R, RemoteFailure> =
            action.toResult(transport(action.toRequest()))
    }



    val api = API(JavaHttpClient())

    val result: Result<String, RemoteFailure> = api.reverse("hello")



    val b = result.map { println(it) }







