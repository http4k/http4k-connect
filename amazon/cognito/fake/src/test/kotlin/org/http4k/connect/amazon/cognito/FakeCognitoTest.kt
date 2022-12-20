package org.http4k.connect.amazon.cognito

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.successValue
import org.http4k.core.Credentials
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.security.AccessTokenResponse
import org.http4k.security.oauth.client.OAuthClientCredentials
import org.http4k.security.oauth.server.OAuthServerMoshi
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class FakeCognitoTest : CognitoContract(FakeCognito()) {
    override val aws = fakeAwsEnvironment

    @Test
    fun `can get access token using client credentials`() {

        val id = cognito.createUserPool(poolName).successValue().UserPool.Id!!

        val client = ClientFilters.OAuthClientCredentials(Credentials(id.value, "bar"))
            .then(ClientFilters.BasicAuth(Credentials(id.value, "bar")))
            .then(http)

        val response = client(Request(POST, "/oauth2/token"))
        val token = OAuthServerMoshi.autoBody<AccessTokenResponse>().toLens()(response)
        assertThat(token.token_type, equalTo("Bearer"))
        assertThat(token.expires_in, equalTo(3600))
    }
}
