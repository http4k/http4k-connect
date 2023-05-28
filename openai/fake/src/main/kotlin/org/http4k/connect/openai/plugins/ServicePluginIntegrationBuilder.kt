package org.http4k.connect.openai.plugins

import org.http4k.connect.openai.auth.OpenAIPluginId
import org.http4k.connect.openai.plugins.internal.ForwardCallsToPluginServer
import org.http4k.connect.openai.plugins.internal.LoadOpenApi
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.routing.routes
import java.time.Clock

/**
 * Plugin implementation which plugs into the FakeOpenAI server. It performs the
 * oauth flow to the plugin, obtaining a token and driving the login.
 */
fun ServicePluginIntegrationBuilder(
    securityTokenFilter: Filter,
    pluginId: OpenAIPluginId,
    pluginUri: Uri
) = object : PluginIntegrationBuilder {
    override val pluginId = pluginId

    override fun buildIntegration(openAiUrl: Uri, http: HttpHandler, clock: Clock) =
        PluginIntegration(
            pluginId,
            routes(
                LoadOpenApi(pluginId, openAiUrl, http, pluginUri),
                ForwardCallsToPluginServer(pluginId, http, pluginUri) {
                    securityTokenFilter
                }
            ),
            securityTokenFilter
        )
}
