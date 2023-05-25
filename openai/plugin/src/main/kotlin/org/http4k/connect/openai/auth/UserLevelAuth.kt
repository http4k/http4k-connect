package org.http4k.connect.openai.auth

import org.http4k.routing.RoutingHttpHandler

class UserLevelAuth(pluginToken: AuthToken) : PluginAuth {
    override val manifestDescription =  mapOf(
        "type" to "user_http",
        "authorization_type" to pluginToken.type
    )

    override val securityFilter = pluginToken.securityFilter
    override val authRoutes = emptyList<RoutingHttpHandler>()
}
