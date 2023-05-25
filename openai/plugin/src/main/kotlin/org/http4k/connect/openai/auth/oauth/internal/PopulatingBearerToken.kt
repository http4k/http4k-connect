package org.http4k.connect.openai.auth.oauth.internal

import org.http4k.connect.openai.auth.oauth.OAuthMachinery
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import org.http4k.security.AccessToken

class PopulatingBearerToken<Principal : Any>(
    private val machinery: OAuthMachinery<Principal>,
    private val apiPrincipalKey: RequestContextLens<Principal>
) : Filter {

    override fun invoke(next: HttpHandler): HttpHandler = {
        when (val principal = it.bearerToken()?.let { machinery[AccessToken(it)] }) {
            null -> Response(UNAUTHORIZED)
            else -> next(it.with(apiPrincipalKey of principal))
        }
    }

    private fun Request.bearerToken(): String? = header("Authorization")
        ?.trim()
        ?.takeIf { it.startsWith("Bearer") }
        ?.substringAfter("Bearer")
        ?.trim()
}
