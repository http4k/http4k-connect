package org.http4k.connect.openai.auth.oauth

import org.http4k.core.Credentials
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.security.oauth.server.ClientId
import org.http4k.security.oauth.server.ClientValidator

internal fun StaticOpenAiClientValidator(config: OAuthConfig) = object : ClientValidator {
    override fun validateClientId(request: Request, clientId: ClientId) =
        clientId == ClientId(config.openAiClientCredentials.user)

    override fun validateCredentials(request: Request, clientId: ClientId, clientSecret: String) =
        Credentials(clientId.value, clientSecret) == config.openAiClientCredentials

    override fun validateRedirection(request: Request, clientId: ClientId, redirectionUri: Uri) =
        config.redirectionUris.contains(redirectionUri)

    override fun validateScopes(request: Request, clientId: ClientId, scopes: List<String>) =
        scopes == listOf(config.scope)
}
