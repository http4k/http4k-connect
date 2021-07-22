package org.http4k.connect.amazon.cognito

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.cognito.model.AccessToken
import org.http4k.connect.amazon.cognito.model.AttributeType
import org.http4k.connect.amazon.cognito.model.AuthFlow.USER_PASSWORD_AUTH
import org.http4k.connect.amazon.cognito.model.ChallengeName.NEW_PASSWORD_REQUIRED
import org.http4k.connect.amazon.cognito.model.ClientName
import org.http4k.connect.amazon.cognito.model.OAuthFlow.client_credentials
import org.http4k.connect.amazon.cognito.model.PoolName
import org.http4k.connect.amazon.cognito.model.UserCode
import org.http4k.connect.amazon.core.model.Password
import org.http4k.connect.amazon.core.model.Username
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class CognitoContract(http: HttpHandler) : AwsContract() {
    private val cognito by lazy {
        Cognito.Http(aws.region, { aws.credentials }, http)
    }

    private val poolName = PoolName.of(UUID.randomUUID().toString())

    @Test
    @Disabled
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
