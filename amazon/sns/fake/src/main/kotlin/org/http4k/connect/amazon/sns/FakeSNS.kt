package org.http4k.connect.amazon.sns

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes

class FakeSNS(
) : ChaosFake() {

    override val app = routes(
        "/" bind { req: Request -> Response(OK) }
    )

    /**
     * Convenience function to get a SNS client
     */
    fun client() = SNS.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this)
}

fun main() {
    FakeSNS().start()
}
