package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.systemsmanager.model.ParameterType
import org.http4k.connect.amazon.systemsmanager.model.SSMParameterName
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.routing.routes

data class StoredParameter(val name: SSMParameterName, val value: String, val type: ParameterType)

class FakeSystemsManager(
    parameters: Storage<StoredParameter> = Storage.InMemory()
) : ChaosFake() {

    private val api = AmazonJsonFake(SystemsManagerMoshi, AwsService.of("AmazonSSM"))

    override val app = routes(
        api.deleteParameter(parameters),
        api.getParameter(parameters),
        api.putParameter(parameters)
    )

    /**
     * Convenience function to get SystemsManager client
     */
    fun client() = SystemsManager.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this)
}

fun main() {
    FakeSystemsManager().start()
}
