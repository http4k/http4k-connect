package org.http4k.connect.openai.auth

import org.http4k.connect.openai.auth.oauth.OAuthMachinery
import org.http4k.connect.openai.auth.oauth.internal.MachineryAccessTokens
import org.http4k.connect.openai.auth.oauth.internal.MachineryAuthorizationCodes
import org.http4k.connect.openai.auth.oauth.internal.PopulatingBearerToken
import org.http4k.connect.openai.auth.oauth.internal.StaticOpenAiClientValidator
import org.http4k.connect.openai.model.AuthedSystem
import org.http4k.connect.openai.model.VerificationToken
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters.InitialiseRequestContext
import org.http4k.lens.Header
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import org.http4k.routing.bind
import org.http4k.security.oauth.server.OAuthServer
import java.time.Clock

class OAuth<T : Any>(
    baseUrl: Uri,
    config: OAuthConfig,
    machinery: OAuthMachinery<T>,
    apiPrincipalKey: RequestContextLens<T>,
    authChallenge: AuthChallenge<T>,
    tokens: Map<AuthedSystem, VerificationToken>,
    clock: Clock,
) : PluginAuth {

    override val manifestDescription = mapOf(
        "type" to "oauth",
        "client_url" to baseUrl.path("/authorize"),
        "scope" to config.scope,
        "authorization_url" to baseUrl.path("/token"),
        "authorization_content_type" to config.contentType,
        "verification_tokens" to tokens
    )

    private val codeContexts = RequestContexts()

    private val codePrincipal = RequestContextKey.required<T>(codeContexts)

    private val server = OAuthServer(
        "/token",
        machinery,
        StaticOpenAiClientValidator(config),
        MachineryAuthorizationCodes(machinery, codePrincipal),
        MachineryAccessTokens(machinery),
        clock,
        refreshTokens = machinery
    )

    override val securityFilter = PopulatingBearerToken(machinery, apiPrincipalKey)

    override val authRoutes = listOf(
        server.tokenRoute,
        "/authorize" bind GET to server.authenticationStart.then(authChallenge.challenge),
        "/authorize" bind POST to InitialiseRequestContext(codeContexts)
            .then { request ->
                when (val principal = authChallenge(request)) {
                    null -> Response(SEE_OTHER).with(Header.LOCATION of request.uri)
                    else -> server.authenticationComplete(request.with(codePrincipal of principal))
                }
            }
    )
}

