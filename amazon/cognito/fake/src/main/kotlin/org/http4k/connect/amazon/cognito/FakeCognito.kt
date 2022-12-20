package org.http4k.connect.amazon.cognito

import org.http4k.aws.AwsCredentials
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.cognito.action.CreateResourceServer
import org.http4k.connect.amazon.cognito.action.CreateUserPoolClient
import org.http4k.connect.amazon.cognito.endpoints.clientCredentialsToken
import org.http4k.connect.amazon.cognito.endpoints.createResourceServer
import org.http4k.connect.amazon.cognito.endpoints.createUserPool
import org.http4k.connect.amazon.cognito.endpoints.createUserPoolClient
import org.http4k.connect.amazon.cognito.endpoints.createUserPoolDomain
import org.http4k.connect.amazon.cognito.endpoints.deleteUserPool
import org.http4k.connect.amazon.cognito.endpoints.deleteUserPoolDomain
import org.http4k.connect.amazon.cognito.model.CloudFrontDomain
import org.http4k.connect.amazon.cognito.model.PoolName
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.routing.routes
import java.time.Clock
import java.time.Duration

class FakeCognito(
    pools: Storage<CognitoPool> = Storage.InMemory(),
    clock: Clock = Clock.systemUTC(),
    expiry: Duration = Duration.ofHours(1)
) : ChaoticHttpHandler() {

    private val api = AmazonJsonFake(CognitoMoshi, AwsService.of("AWSCognitoIdentityProviderService"))

    override val app = routes(
        clientCredentialsToken(clock, expiry, pools),
        api.createUserPool(pools),
        api.createResourceServer(pools),
        api.createUserPoolDomain(pools),
        api.createUserPoolClient(pools),
        api.deleteUserPoolDomain(pools),
        api.deleteUserPool(pools)
    )

    /**
     * Convenience function to get Cognito client
     */
    fun client() = Cognito.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this)
}

fun main() {
    FakeCognito().start()
}

data class CognitoPool(
    val name: PoolName,
    val clients: MutableList<CreateUserPoolClient> = mutableListOf(),
    val domains: MutableList<CloudFrontDomain> = mutableListOf(),
    val resourceServers: MutableList<CreateResourceServer> = mutableListOf()
)
