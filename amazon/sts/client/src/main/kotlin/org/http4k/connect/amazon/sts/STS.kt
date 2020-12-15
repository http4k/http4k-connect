package org.http4k.connect.amazon.sts

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.sts.action.STSAction

/**
 * Docs: https://docs.aws.amazon.com/STS/latest/APIReference/welcome.html
 */
@Http4kConnectAdapter
interface STS {
    /**
     * Available actions:
     *  AssumeRole
     */
    operator fun <R> invoke(request: STSAction<R>): Result<R, RemoteFailure>

    companion object
}
