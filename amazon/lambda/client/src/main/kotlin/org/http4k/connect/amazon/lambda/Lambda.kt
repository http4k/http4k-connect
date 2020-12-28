package org.http4k.connect.amazon.lambda

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.lambda.action.InvokeFunction
import org.http4k.connect.amazon.model.LambdaName
import org.http4k.format.Moshi

/**
 * Docs: https://docs.aws.amazon.com/lambda/latest/dg/welcome.html
 */
interface Lambda {
    operator fun <REQ : Any, RESP : Any> invoke(request: InvokeFunction<REQ, RESP>): Result<RESP, RemoteFailure>

    companion object
}

inline fun <reified REQ : Any, reified RESP : Any> Lambda.invokeFunction(
    name: LambdaName, req: REQ): Result<RESP, RemoteFailure> = this(InvokeFunction(name, req, autoMarshalling = Moshi))

