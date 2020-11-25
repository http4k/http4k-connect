package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock

class FakeSystemsManager(
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    override val app = routes(
        "/" bind GET to { req: Request -> Response(Status.OK) }
    )

    /**
     * Convenience function to get SystemsManager client
     */
    fun client() = SystemsManager.Http(
        AwsCredentialScope("*", "s3"),
        { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeSystemsManager().start()
}
