package org.http4k.connect.amazon.cognito

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.cognito.action.AuthInitiated
import org.http4k.connect.amazon.cognito.model.AccessToken
import org.http4k.connect.amazon.cognito.model.IdToken
import org.http4k.connect.amazon.cognito.model.RefreshToken
import org.junit.jupiter.api.Test

class ActualCognitoResponsesTest {

    val factory = CognitoMoshi

    @Test
    fun `deserialising a user password response with no challenges requested`() {
        val response = factory.asA<AuthInitiated>(
            """{"AuthenticationResult":
            {"AccessToken":"access-token",
            "ExpiresIn":3600,
            "IdToken":"id-token",
            "RefreshToken":"refresh-token",
            "TokenType":"Bearer"},
            "ChallengeParameters":{}}
        """.trimIndent()
        )
        assertThat(response.AuthenticationResult.AccessToken, equalTo(AccessToken.of("access-token")))
        assertThat(response.AuthenticationResult.ExpiresIn, equalTo(3600))
        assertThat(response.AuthenticationResult.IdToken, equalTo(IdToken.of("id-token")))
        assertThat(response.AuthenticationResult.RefreshToken, equalTo(RefreshToken.of("refresh-token")))
        assertThat(response.AuthenticationResult.TokenType, equalTo("Bearer"))
        assertThat(response.ChallengeParameters, equalTo(mapOf()))
    }
}
