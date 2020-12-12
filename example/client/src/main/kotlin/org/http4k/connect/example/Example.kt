package org.http4k.connect.example

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

interface Example {
    operator fun invoke(request: Echo): Result<Echoed, RemoteFailure>

    companion object
}
