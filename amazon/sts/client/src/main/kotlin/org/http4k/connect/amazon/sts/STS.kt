package org.http4k.connect.amazon.sts

import dev.forkhandles.result4k.Result
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/STS/latest/APIReference/welcome.html
 */
interface STSAction<R> : Action<R>

interface STS {
    operator fun <R> invoke(request: STSAction<R>): Result<R, RemoteFailure>

    companion object
}

