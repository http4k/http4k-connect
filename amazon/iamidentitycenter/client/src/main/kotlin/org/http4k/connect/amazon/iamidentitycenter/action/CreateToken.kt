package org.http4k.connect.amazon.iamidentitycenter.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.core.model.AccessToken
import org.http4k.connect.amazon.core.model.ClientId
import org.http4k.connect.amazon.core.model.ClientSecret
import org.http4k.connect.amazon.core.model.IdToken
import org.http4k.connect.amazon.core.model.RefreshToken
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.model.Code
import org.http4k.connect.amazon.model.DeviceCode
import org.http4k.connect.amazon.model.Scope
import org.http4k.connect.amazon.model.SessionId
import org.http4k.connect.amazon.model.TokenType
import org.http4k.connect.kClass
import org.http4k.core.Uri
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class CreateToken(
    val clientId: ClientId,
    val clientSecret: ClientSecret,
    val code: Code? = null,
    val deviceCode: DeviceCode? = null,
    val redirectUri: Uri? = null,
    val refreshToken: RefreshToken? = null,
    val scope: List<Scope>? = null
) : IAMIdentityCenterAutomarshalledAction<CreatedToken>(kClass()) {
    val grantType = "urn:ietf:params:oauth:grant-type:device_code"
    override fun uri() = Uri.of("/token")
}

@JsonSerializable
data class CreatedToken(
    val accessToken: AccessToken,
    val expiresIn: Timestamp,
    val idToken: IdToken,
    val refreshToken: RefreshToken,
    val aws_sso_app_session_id: SessionId?,
    val issuedTokenType: TokenType?,
    val originSessionId: SessionId?,
) {
    val tokenType = "Bearer"
}
