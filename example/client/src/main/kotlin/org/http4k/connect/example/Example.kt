package org.http4k.connect.example

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure

@Http4kConnectAdapter
interface Example {
    operator fun <R> invoke(action: ExampleAction<R>): Result<R, RemoteFailure>

    companion object
}
