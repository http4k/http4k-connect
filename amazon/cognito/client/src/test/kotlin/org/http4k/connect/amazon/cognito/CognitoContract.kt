package org.http4k.connect.amazon.cognito

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.cognito.model.AttributeType
import org.http4k.connect.amazon.cognito.model.ClientName
import org.http4k.connect.amazon.cognito.model.Password
import org.http4k.connect.amazon.cognito.model.PoolName
import org.http4k.connect.amazon.cognito.model.Username
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.filter.debug
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class CognitoContract(http: HttpHandler) : AwsContract() {
    private val cognito by lazy {
        Cognito.Http(aws.region, { aws.credentials }, http.debug())
    }

    private val poolName = PoolName.of(UUID.randomUUID().toString())

    @Test
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
    fun `user auth lifecycle`() {
        val id = cognito.createUserPool(poolName).successValue().UserPool.Id!!

        try {
            val username = Username.of(UUID.randomUUID().toString())
            cognito.adminCreateUser(
                username, id, listOf(
                    AttributeType("email", "test@example.com"),
                    AttributeType("email_verified", "true")
                )
            ).successValue()

            val client = cognito.createUserPoolClient(id, ClientName.of(username.value)).successValue().UserPoolClient
            cognito.deleteUserPoolClient(id, client.ClientId).successValue()

//            cognito.initiateAuth(client.ClientId, USER_PASSWORD_AUTH).successValue()

            cognito.adminDeleteUser(username, id).successValue()
        } finally {
            cognito.deleteUserPool(id).successValue()
        }
    }
}
