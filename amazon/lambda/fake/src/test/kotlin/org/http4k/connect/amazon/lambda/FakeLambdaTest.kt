package org.http4k.connect.amazon.lambda

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK

private val Reverser = { req: Request -> Response(OK).body(req.bodyString().reversed()) }

class FakeLambdaTest : LambdaContract(FakeLambda(reverse to Reverser)) {
    override val aws = fakeAwsEnvironment
}
