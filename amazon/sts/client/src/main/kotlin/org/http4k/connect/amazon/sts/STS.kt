package org.http4k.connect.amazon.sts

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/STS/latest/APIReference/welcome.html
 */
interface STS {
    operator fun invoke(request: AssumeRole): Result<AssumedRole, RemoteFailure>

    companion object
}

