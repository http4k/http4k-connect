package org.http4k.connect.openai.auth

import org.http4k.connect.openai.model.AuthedSystem
import org.http4k.connect.openai.model.VerificationToken
import org.http4k.routing.RoutingHttpHandler

class ServiceAuth(
    pluginToken: PluginToken,
    tokens: Map<AuthedSystem, VerificationToken>
) : PluginAuth {
    override val manifestDescription =  mapOf(
        "type" to "service_http",
        "authorization_type" to pluginToken.type,
        "verification_tokens" to tokens
    )

    override val securityFilter = pluginToken.securityFilter
    override val authRoutes = emptyList<RoutingHttpHandler>()
}
