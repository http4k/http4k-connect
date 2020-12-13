package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.systemsmanager.action.DeleteParameter
import org.http4k.connect.amazon.systemsmanager.action.GetParameter
import org.http4k.connect.amazon.systemsmanager.action.Parameter
import org.http4k.connect.amazon.systemsmanager.action.ParameterValue
import org.http4k.connect.amazon.systemsmanager.action.PutParameter
import org.http4k.connect.amazon.systemsmanager.action.PutParameterResult
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.routing.routes
import java.time.Clock

data class StoredParameter(val name: String, val value: String, val type: ParameterType)

class FakeSystemsManager(
    private val parameters: Storage<StoredParameter> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    private val api = AmazonJsonFake(SystemsManagerMoshi, AwsService.of("AmazonSSM"))

    override val app = routes(
        deleteParameter(),
        getParameter(),
        putParameter()
    )

    private fun deleteParameter() = api.route<DeleteParameter> { req ->
        parameters[req.Name]?.let {
            parameters.remove(req.Name)
            Unit
        }
    }


    private fun getParameter() = api.route<GetParameter> { req ->
        parameters[req.Name]?.let {
            ParameterValue(Parameter(
                ARN.of(Region.of("us-east-1"), AwsService.of("ssm"), "parameter", it.name, AwsAccount.of("0")),
                it.name, it.value, it.type, null, 1, Timestamp.of(0), null, null))
        }
    }

    private fun putParameter() = api.route<PutParameter> { req ->
        when {
            parameters[req.Name] == null -> {
                parameters[req.Name] = StoredParameter(req.Name, req.Value, req.Type)
                PutParameterResult("Standard", 1)
            }
            else -> null
        }
    }


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
