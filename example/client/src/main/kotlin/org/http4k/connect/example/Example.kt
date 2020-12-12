package org.http4k.connect.example

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

interface Example {
    operator fun <R : Any> invoke(request: ExampleAction<R>): Result<R, RemoteFailure>

    companion object
}
