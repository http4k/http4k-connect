package org.http4k.connect.amazon.cognito

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.cognito.action.Scope
import org.http4k.connect.amazon.cognito.model.AccessToken
import org.http4k.connect.amazon.cognito.model.AttributeType
import org.http4k.connect.amazon.cognito.model.AuthFlow.USER_PASSWORD_AUTH
import org.http4k.connect.amazon.cognito.model.ChallengeName.NEW_PASSWORD_REQUIRED
import org.http4k.connect.amazon.cognito.model.ClientName
import org.http4k.connect.amazon.cognito.model.CloudFrontDomain
import org.http4k.connect.amazon.cognito.model.OAuthFlow.client_credentials
import org.http4k.connect.amazon.cognito.model.Password
import org.http4k.connect.amazon.cognito.model.PoolName
import org.http4k.connect.amazon.cognito.model.UserCode
import org.http4k.connect.amazon.cognito.model.Username
import org.http4k.connect.successValue
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.debug
import org.http4k.security.AccessTokenResponse
import org.http4k.security.oauth.client.OAuthClientCredentials
import org.http4k.security.oauth.server.OAuthServerMoshi
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class CognitoContract(private val http: HttpHandler) : AwsContract() {
    private val cognito by lazy {
        Cognito.Http(aws.region, { aws.credentials }, http.debug())
    }

    private val poolName = PoolName.of(UUID.randomUUID().toString())

    @Test
    @Disabled
    fun `delete pools`() {
        cognito.listUserPools(60).successValue().UserPools.forEach {
            cognito.deleteUserPool(it.Id)
        }
    }

    @Test
    @Disabled
    open fun `can get access token using client credentials`() {
        val id = cognito.createUserPool(poolName).successValue().UserPool.Id!!

        try {
            cognito.createResourceServer(id, "scope", "scope", listOf(Scope("Name", "Description"))).successValue()
            val domain = CloudFrontDomain.of(UUID.randomUUID().toString())
            cognito.createUserPoolDomain(id, domain).successValue()

            try {
                val poolClient = cognito.createUserPoolClient(
                    UserPoolId = id,
                    ClientName = ClientName.of(UUID.randomUUID().toString()),
                    AllowedOAuthFlows = listOf(client_credentials),
                    AllowedOAuthFlowsUserPoolClient = true,
                    AllowedOAuthScopes = listOf("scope/Name"),
                    GenerateSecret = true
                ).successValue().UserPoolClient

                val clientCredentials = Credentials(poolClient.ClientId.value, poolClient.ClientSecret!!.value)

                val client = ClientFilters.OAuthClientCredentials(clientCredentials, listOf("scope/Name"))
                    .then(ClientFilters.BasicAuth(clientCredentials))
                    .then(ClientFilters.SetBaseUriFrom(Uri.of("https://$domain.auth.${aws.region}.amazoncognito.com")))
                    .then(http.debug())

                val response = client(Request(POST, "/oauth2/token"))
                val token = OAuthServerMoshi.autoBody<AccessTokenResponse>().toLens()(response)
                assertThat(response.bodyString(), response.status.successful, equalTo(true))
                assertThat(token.token_type, equalTo("Bearer"))
                assertThat(token.expires_in, equalTo(3600))
            } finally {
                cognito.deleteUserPoolDomain(id, domain)
            }

        } finally {
            cognito.deleteUserPool(id).successValue()
        }
    }

    @Test
    @Disabled("WIP")
    fun `user pool lifecycle`() {
        val id = cognito.createUserPool(poolName).successValue().UserPool.Id!!

        try {
            val username = Username.of(UUID.randomUUID().toString())
            cognito.adminCreateUser(
                username, id, listOf(
                    AttributeType("email", "test@example.com"),
                    AttributeType("email_verified", "true")
                )
            ).successValue()

            assertThat(cognito.adminGetUser(username, id).successValue().Username, equalTo(username))

            cognito.adminSetUserPassword(username, id, true, Password.of("Password1£$%4")).successValue()

            cognito.adminResetUserPassword(username, id, emptyMap()).successValue()

            cognito.adminDisableUser(username, id).successValue()

            cognito.adminEnableUser(username, id).successValue()

            cognito.adminDeleteUser(username, id).successValue()
        } finally {
            cognito.deleteUserPool(id).successValue()
        }
    }

    @Test
    @Disabled("WIP")
    fun `user auth lifecycle`() {
        val username = Username.of(UUID.randomUUID().toString())
        val id = cognito.createUserPool(poolName).successValue().UserPool.Id!!

        try {
            cognito.adminCreateUser(
                username, id, listOf(
                    AttributeType("email", "test@example.com"),
                    AttributeType("email_verified", "true")
                )
            ).successValue()

            val client = cognito.createUserPoolClient(id, ClientName.of(username.value), listOf(client_credentials))
                .successValue().UserPoolClient

            val challenge = cognito.initiateAuth(
                client.ClientId, USER_PASSWORD_AUTH, mapOf(
                    "USERNAME" to username.value,
                    "PASSWORD" to "foobar"
                )
            ).successValue()

            cognito.associateSoftwareToken(AccessToken.of("1234"), challenge.Session).successValue()

            cognito.verifySoftwareToken(UserCode.of("123456"), AccessToken.of("1234"), challenge.Session).successValue()

            val nextChallenge = cognito.respondToAuthChallenge(
                client.ClientId, NEW_PASSWORD_REQUIRED, mapOf(
                    NEW_PASSWORD_REQUIRED to "",
                )
            ).successValue()

            cognito.deleteUserPoolClient(id, client.ClientId).successValue()

            cognito.adminDeleteUser(username, id).successValue()
        } finally {
            cognito.deleteUserPool(id).successValue()
        }
    }
}
