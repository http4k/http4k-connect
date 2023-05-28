package org.http4k.connect.openai.plugins

import org.http4k.client.JavaHttpClient
import org.http4k.connect.openai.auth.OpenAIPluginId
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.routing.RoutingHttpHandler
import java.time.Clock

interface PluginIntegrationBuilder {
    val pluginId: OpenAIPluginId

    fun buildIntegration(
        openAiUrl: Uri,
        http: HttpHandler = JavaHttpClient(),
        clock: Clock = Clock.systemUTC()
    ): PluginIntegration
}

data class PluginIntegration(
    val pluginId: OpenAIPluginId,
    val httpHandler: RoutingHttpHandler,
    val filter: Filter
)
