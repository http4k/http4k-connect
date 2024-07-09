package org.http4k.connect.lmstudio

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure

@Http4kConnectAdapter
interface LmStudio {
    operator fun <R> invoke(action: LmStudioAction<R>): Result<R, RemoteFailure>

    companion object
}
