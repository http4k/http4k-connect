package addressbook.oauth.auth

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.openai.auth.oauth.PrincipalStore
import org.http4k.connect.openai.auth.oauth.SecureStrings
import org.http4k.security.AccessToken
import org.http4k.security.oauth.core.RefreshToken
import org.http4k.security.oauth.server.AccessTokens
import org.http4k.security.oauth.server.AuthorizationCode
import org.http4k.security.oauth.server.ClientId
import org.http4k.security.oauth.server.TokenRequest
import org.http4k.security.oauth.server.UnsupportedGrantType
import org.http4k.security.oauth.server.accesstoken.AuthorizationCodeAccessTokenRequest
import java.time.Duration

fun <Principal : Any> StorageAccessTokens(
    principleStore: PrincipalStore<Principal>,
    strings: SecureStrings,
    tokenLifespan: Duration,
) = object : AccessTokens {
    override fun create(clientId: ClientId, tokenRequest: TokenRequest) =
        Failure(UnsupportedGrantType("client_credentials"))

    override fun create(
        clientId: ClientId,
        tokenRequest: AuthorizationCodeAccessTokenRequest,
        authorizationCode: AuthorizationCode
    ) = Success(
        AccessToken(
            strings(),
            expiresIn = tokenLifespan.seconds,
            scope = tokenRequest.scopes.joinToString(" "),
            refreshToken = RefreshToken(strings())
        ).also { token ->
            principleStore[authorizationCode]?.also { principleStore[token] = it }
        }
    )
}
