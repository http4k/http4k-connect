package org.http4k.connect.amazon.lambda

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.model.LambdaName
import org.http4k.core.HttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock

class FakeLambda(
    vararg lambdas: Pair<LambdaName, HttpHandler>,
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    override val app = routes(
        *lambdas.map { (name, lambda) ->
            "/2015-03-31/functions/$name/invocations" bind lambda }.toTypedArray()
    )

    /**
     * Convenience function to get Lambda client
     */
    fun client() = Lambda.Http(
        AwsCredentialScope("*", "lambda"),
        { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeLambda().start()
}

