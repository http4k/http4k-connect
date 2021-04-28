package org.http4k.connect.amazon.cognito

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.core.model.Region
import java.time.Clock
import java.time.Clock.systemUTC

class FakeCognito(
    private val clock: Clock = systemUTC()
) : ChaosFake() {

    override val app = { req: org.http4k.core.Request -> org.http4k.core.Response(org.http4k.core.Status.OK) }

    /**
     * Convenience function to get Cognito client
     */
    fun client() = Cognito.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this)
}

fun main() {
    FakeCognito().start()
}
