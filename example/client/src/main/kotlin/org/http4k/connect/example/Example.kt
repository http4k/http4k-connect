package org.http4k.connect.example

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

interface Example {
    fun echo(input: String): Result<String, RemoteFailure>

    companion object
}
