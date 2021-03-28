package org.http4k.connect.amazon.cloudfront

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.routing.routes
import java.time.Clock
import java.time.Clock.systemUTC


class FakeCloudFront(
    private val clock: Clock = systemUTC()
) : ChaosFake() {

    override val app = routes(
        CreateInvalidation(clock)
    )

    /**
     * Convenience function to get CloudFront client
     */
    fun client() = CloudFront.Http({ AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeCloudFront().start()
}
