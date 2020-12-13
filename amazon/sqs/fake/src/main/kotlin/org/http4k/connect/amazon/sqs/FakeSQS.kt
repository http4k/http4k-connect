package org.http4k.connect.amazon.sqs

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import java.time.Clock

class FakeSQS(private val clock: Clock = Clock.systemDefaultZone()) : ChaosFake() {
    private val lens = Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()

    override val app = routes(
        "/" bind POST to { _: Request -> Response(Status.OK) })

    /**
     * Convenience function to get a SQS client
     */
    fun client() = SQS.Http(
        AwsCredentialScope("*", "sqs"),
        { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeSQS().start()
}
