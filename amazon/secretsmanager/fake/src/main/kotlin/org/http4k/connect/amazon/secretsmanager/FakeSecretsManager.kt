package org.http4k.connect.amazon.secretsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock

class FakeSecretsManager(private val clock: Clock = Clock.systemDefaultZone()) : ChaosFake() {
    override val app = routes(
        "/" bind routes(
            GET to {
                Response(INTERNAL_SERVER_ERROR)
            },
            POST to {
                Response(INTERNAL_SERVER_ERROR)
            }
        )
    )

    /**
     * Convenience function to get SecretsManager client
     */
    fun client() = SecretsManager.Http(
        AwsCredentialScope("*", "s3"),
        { AwsCredentials("accessKey", "secret") }, this, clock)

}

fun main() {
    FakeSecretsManager().start()
}
