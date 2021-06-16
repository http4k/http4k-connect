package org.http4k.connect.amazon.cognito

import org.http4k.aws.AwsCredentials
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.amazon.core.model.Region
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import java.time.Clock
import java.time.Clock.systemUTC

class FakeCognito(
    private val clock: Clock = systemUTC()
) : ChaoticHttpHandler() {

    override val app = { req: Request -> Response(OK).body("{}") }

    /**
     * Convenience function to get Cognito client
     */
    fun client() = Cognito.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this)
}

fun main() {
    FakeCognito().start()
}
