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
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.routing.routes
import java.time.Clock

data class Parameter(val name: String, val value: String, val type: ParameterType)

class FakeSystemsManager(
    private val parameters: Storage<Parameter> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    private val api = AmazonJsonFake(SystemsManagerJackson, AwsService.of("AmazonSSM"))

    override val app = routes(
        deleteParameter(),
        getParameter(),
        putParameter()
    )

    private fun deleteParameter() = api.route<DeleteParameter, DeleteParameter.Request> { req ->
        parameters[req.Name]?.let {
            parameters.remove(req.Name)
            Unit
        }
    }


    private fun getParameter() = api.route<GetParameter, GetParameter.Request> { req ->
        parameters[req.Name]?.let {
            GetParameter.Response(GetParameter.Parameter(
                ARN.of(Region.of("us-east-1"), AwsService.of("ssm"), "parameter", it.name, AwsAccount.of("0")),
                it.name, "String", Timestamp.of(0), "", "", it.type, it.value, 1))
        }
    }

    private fun putParameter() = api.route<PutParameter, PutParameter.Request> { req ->
        when {
            parameters[req.Name] == null -> {
                parameters[req.Name] = Parameter(req.Name, req.Value, req.Type)
                PutParameter.Response("Standard", 1)
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
