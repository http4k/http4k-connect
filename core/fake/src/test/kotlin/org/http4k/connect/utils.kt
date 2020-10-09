package org.http4k.connect

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.Assume.assumeTrue
import java.lang.Runtime.getRuntime

class CapturingHttpHandler : HttpHandler {
    var captured: Request? = null
    var response: Response = Response(Status.OK)

    override fun invoke(request: Request): Response {
        captured = request
        return response
    }
}

fun <T, E> Result<T, E>.successValue(): T = when (this) {
    is Success -> value
    is Failure -> throw AssertionError("Failed: $reason")
}

fun assumeDockerDaemonRunning() {
    assumeTrue("Docker is not running", getRuntime().exec("docker ps").errorStream.bufferedReader().readText().isEmpty())
}
