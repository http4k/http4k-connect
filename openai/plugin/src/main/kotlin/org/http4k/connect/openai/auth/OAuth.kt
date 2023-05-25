package org.http4k.connect.openai.auth

import org.http4k.connect.openai.auth.oauth.OAuthMachinery
import org.http4k.connect.openai.model.AuthedSystem
import org.http4k.connect.openai.model.VerificationToken
import org.http4k.core.ContentType
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.Credentials
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.routing.bind
import org.http4k.security.oauth.server.ClientId
import org.http4k.security.oauth.server.ClientValidator
import org.http4k.security.oauth.server.OAuthServer
import java.time.Clock

class OAuth(
    baseUrl: Uri,
    tokens: Map<AuthedSystem, VerificationToken>,
    openAiClientCredentials: Credentials,
    storage: OAuthMachinery,
    clock: Clock,
    scope: String = "",
    contentType: ContentType = APPLICATION_JSON.withNoDirectives(),
) : PluginAuth {

    override val manifestDescription = mapOf(
        "type" to "oauth",
        "client_url" to baseUrl.path("/authorize"),
        "scope" to scope,
        "authorization_url" to baseUrl.path("/token"),
        "authorization_content_type" to contentType,
        "verification_tokens" to tokens
    )

    override val securityFilter = AuthToken.Bearer(storage::validate).securityFilter

    private val server = OAuthServer(
        "/token",
        storage,
        StaticOpenAiClientValidator(openAiClientCredentials, scope),
        storage,
        storage,
        clock,
        refreshTokens = storage
    )

    override val authRoutes = listOf(
        server.tokenRoute,
        "/authorize" bind GET to server.authenticationStart.then(server.authenticationComplete)
    )
}

internal class StaticOpenAiClientValidator(
    private val openAiClientCredentials: Credentials,
    private val scope: String
) : ClientValidator {
    override fun validateClientId(request: Request, clientId: ClientId) =
        clientId == ClientId(openAiClientCredentials.user)

    override fun validateCredentials(request: Request, clientId: ClientId, clientSecret: String) =
        Credentials(clientId.value, clientSecret) == openAiClientCredentials

    override fun validateRedirection(request: Request, clientId: ClientId, redirectionUri: Uri) =
        redirectionUri == Uri.of("https://chat.openai.com/aip/plugin-some_plugin_id/oauth/callback")

    override fun validateScopes(request: Request, clientId: ClientId, scopes: List<String>) =
        scopes == listOf(scope)
}
