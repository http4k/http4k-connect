package org.http4k.connect.openai.auth.oauth

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import org.http4k.core.ContentType
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.Uri
import org.http4k.security.OAuthProviderConfig

/**
 * Standard OAuth config setup.
 */
data class OAuthPluginConfig(
    val pluginId: PluginId,
    val providerConfig: OAuthProviderConfig,
    val redirectionUris: List<Uri> = listOf(Uri.of("https://chat.openai.com/aip/plugin-${pluginId}/oauth/callback")),
    val scope: String = "",
    val contentType: ContentType = APPLICATION_JSON.withNoDirectives(),
)

class PluginId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<PluginId>(::PluginId)
}
