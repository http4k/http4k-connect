package org.http4k.connect.openai.auth

import org.http4k.connect.openai.auth.oauth.OAuthMachinery
import org.http4k.connect.openai.auth.oauth.internal.StaticOpenAiClientValidator
import org.http4k.connect.openai.model.AuthedSystem
import org.http4k.connect.openai.model.VerificationToken
import org.http4k.core.Filter
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.Uri
import org.http4k.core.queries
import org.http4k.core.then
import org.http4k.core.toParametersMap
import org.http4k.core.with
import org.http4k.lens.Header.LOCATION
import org.http4k.lens.RequestContextLens
import org.http4k.routing.bind
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AuthorizationCode
import org.http4k.security.oauth.server.OAuthServer
import java.time.Clock

class OAuth<T : Any>(
    baseUrl: Uri,
    config: OAuthConfig,
    machinery: OAuthMachinery<T>,
    apiPrincipalKey: RequestContextLens<T>,
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

    private val server = OAuthServer(
        "/token",
        machinery,
        StaticOpenAiClientValidator(config),
        machinery,
        machinery,
        clock,
        refreshTokens = machinery
    )

    override val securityFilter = Filter { next ->
        {
            when (val principal = it.bearerToken()?.let { machinery[AccessToken(it)] }) {
                null -> Response(UNAUTHORIZED)
                else -> next(it.with(apiPrincipalKey of principal))
            }
        }
    }

    override val authRoutes = listOf(
        server.tokenRoute,
        "/authorize" bind GET to server.authenticationStart.then(machinery.challenge),
        "/authorize" bind POST to { request ->
            when (val principal = machinery(request)) {
                null -> Response(SEE_OTHER).with(LOCATION of request.uri)
                else -> server.authenticationComplete(request)
                    .also {
                        LOCATION(it).queries().toParametersMap()["code"]
                            ?.firstOrNull()
                            ?.also { machinery[AuthorizationCode(it)] = principal }
                    }
            }
        }
    )
}

private fun Request.bearerToken(): String? = header("Authorization")
    ?.trim()
    ?.takeIf { it.startsWith("Bearer") }
    ?.substringAfter("Bearer")
    ?.trim()
