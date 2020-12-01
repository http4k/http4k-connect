package org.http4k.connect.amazon.sts

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/STS/latest/APIReference/welcome.html
 */
interface STS {
    fun assumeRole(request: AssumeRole.Request): Result<AssumeRole.Response, RemoteFailure>

    companion object
}

