package org.http4k.connect.openai.auth.oauth.internal

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.peek
import org.http4k.connect.openai.auth.oauth.OAuthMachinery
import org.http4k.security.oauth.server.AccessTokens
import org.http4k.security.oauth.server.AuthorizationCode
import org.http4k.security.oauth.server.ClientId
import org.http4k.security.oauth.server.TokenRequest
import org.http4k.security.oauth.server.UnsupportedGrantType
import org.http4k.security.oauth.server.accesstoken.AuthorizationCodeAccessTokenRequest

internal fun <T : Any> MachineryAccessTokens(machinery: OAuthMachinery<T>) = object : AccessTokens {
    override fun create(clientId: ClientId, tokenRequest: TokenRequest) =
        Failure(UnsupportedGrantType("client_credentials"))

    override fun create(
        clientId: ClientId,
        tokenRequest: AuthorizationCodeAccessTokenRequest,
        authorizationCode: AuthorizationCode
    ) = machinery.create(clientId, tokenRequest, authorizationCode)
        .peek { token ->
            machinery[authorizationCode]
                ?.let { machinery[token] = it }
        }
}
