package org.http4k.connect.example

import dev.forkhandles.result4k.Result
import org.http4k.core.Status

data class RemoteFailure(val status: Status)

interface Example {
    fun echo(input: String): Result<String, RemoteFailure>

    companion object
}
