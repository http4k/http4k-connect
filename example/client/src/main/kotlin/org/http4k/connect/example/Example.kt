package org.http4k.connect.example

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.example.action.ExampleAction

interface Example {
    /**
     * Available actions:
     *  Echo
     */
    operator fun <R : Any> invoke(request: ExampleAction<R>): Result<R, RemoteFailure>

    companion object
}
