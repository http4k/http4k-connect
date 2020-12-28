package org.http4k.connect.amazon.lambda

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.lambda.action.LambdaAction

/**
 * Docs: https://docs.aws.amazon.com/lambda/latest/dg/welcome.html
 */
@Http4kConnectAdapter
interface Lambda {
    operator fun <RESP : Any> invoke(request: LambdaAction<RESP>): Result<RESP, RemoteFailure>

    companion object
}

