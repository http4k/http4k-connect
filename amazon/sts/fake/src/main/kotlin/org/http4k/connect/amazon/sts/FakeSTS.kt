package org.http4k.connect.amazon.sts

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock
import java.time.Duration
import java.time.Duration.ofHours

class FakeSTS(
    private val clock: Clock = Clock.systemUTC(),
    defaultSessionValidity: Duration = ofHours(1)
) : ChaosFake() {

    override val app = routes(
        "/" bind POST to routes(
            assumeRole(defaultSessionValidity, clock)
        )
    )

    /**
     * Convenience function to get a STS client
     */
    fun client() = STS.Http(
        Region.of("ldn-north-1"),
        { AwsCredentials("accessKey", "secret") }, this, clock
    )
}

fun main() {
    FakeSTS().start()
}
