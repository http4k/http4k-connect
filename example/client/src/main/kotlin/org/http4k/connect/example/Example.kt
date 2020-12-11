package org.http4k.connect.example

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

interface Example {
    operator fun invoke(request: EchoRequest): Result<EchoResponse, RemoteFailure>

    companion object
}
