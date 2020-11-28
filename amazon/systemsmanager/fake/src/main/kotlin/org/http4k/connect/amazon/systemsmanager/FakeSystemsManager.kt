package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock

typealias Parameter = String

class FakeSystemsManager(
    private val parameters: Storage<Parameter> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    private val api = AmazonJsonFake(SystemsManagerJackson, AwsService.of("ssm"))

    override val app = routes(
        "/" bind GET to { Response(OK) }
    )

    /**
     * Convenience function to get SystemsManager client
     */
    fun client() = SystemsManager.Http(
        AwsCredentialScope("*", "ssm"),
        { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeSystemsManager().start()
}
