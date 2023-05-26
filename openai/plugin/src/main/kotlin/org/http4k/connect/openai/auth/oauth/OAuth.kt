package org.http4k.connect.openai.auth.oauth

import dev.forkhandles.result4k.peek
import org.http4k.connect.openai.auth.PluginAuth
import org.http4k.connect.openai.model.AuthedSystem
import org.http4k.connect.openai.model.VerificationToken
import org.http4k.core.Filter
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.lens.Header
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import org.http4k.routing.bind
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.AuthRequest
import org.http4k.security.oauth.server.AuthorizationCodes
import org.http4k.security.oauth.server.OAuthServer
import java.time.Clock

/**
 * OAuth plugin auth. Uses an AuthorizationCode grant to auth the user to OpenAI
 */
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
        object : AuthorizationCodes by machinery {
            override fun create(request: Request, authRequest: AuthRequest, response: Response) =
                machinery.create(request, authRequest, response).peek { machinery[it] = principalKey(request) }
        },
        machinery,
        clock,
        refreshTokens = machinery
    )

    private val contexts = RequestContexts()
    private val principalKey = RequestContextKey.required<T>(contexts)

    override val securityFilter = ServerFilters.InitialiseRequestContext(contexts)
        .then(Filter { next ->
            {
                when (it.bearerToken()?.let { machinery[AccessToken(it)] }) {
                    null -> Response(Status.UNAUTHORIZED)
                    else -> next(it.with(apiPrincipalKey of principalKey(it)))
                }
            }
        })

    override val authRoutes = listOf(
        server.tokenRoute,
        "/authorize" bind Method.GET to server.authenticationStart.then(machinery.challenge),
        "/authorize" bind Method.POST to { request ->
            when (val principal = machinery(request)) {
                null -> Response(Status.SEE_OTHER).with(Header.LOCATION of request.uri)
                else -> server.authenticationComplete(request.with(principalKey of principal))
            }
        }
    )
}

private fun Request.bearerToken(): String? = header("Authorization")
    ?.trim()
    ?.takeIf { it.startsWith("Bearer") }
    ?.substringAfter("Bearer")
    ?.trim()
