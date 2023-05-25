package org.http4k.connect.openai.auth.oauth.impl

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.peek
import org.http4k.connect.storage.Storage
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AccessTokens
import org.http4k.security.oauth.server.MissingAuthorizationCode
import org.http4k.security.oauth.server.refreshtoken.RefreshTokens

fun StorageRefreshTokens(
    refreshTokens: Storage<AccessToken>,
    accessTokens: AccessTokens
) = RefreshTokens { clientId, tokenRequest, refreshToken ->
    refreshTokens[refreshToken.value]
        ?.let {
            accessTokens.create(clientId, tokenRequest)
                .peek { refreshTokens[refreshToken.value] = it }
        } ?: Failure(MissingAuthorizationCode)
}
