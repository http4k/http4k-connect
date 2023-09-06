package org.http4k.connect

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.jupiter.api.Assumptions.assumeTrue
import java.lang.Runtime.getRuntime
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

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
    assumeTrue(
        getRuntime().exec(arrayOf("docker", "ps")).errorStream.bufferedReader().readText().isEmpty(),
        "Docker is not running"
    )
}

class TestClock(private var time: Instant = Instant.EPOCH) : Clock() {
    override fun getZone(): ZoneId = TODO("Not yet implemented")

    override fun withZone(zone: ZoneId?): Clock = TODO("Not yet implemented")

    override fun instant(): Instant = time

    fun tickBy(duration: Duration) {
        time = time.plus(duration)
    }
}
