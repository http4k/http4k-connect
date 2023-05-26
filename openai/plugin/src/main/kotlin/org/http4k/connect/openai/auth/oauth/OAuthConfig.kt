package org.http4k.connect.openai.auth.oauth

import org.http4k.core.ContentType
import org.http4k.core.Credentials
import org.http4k.core.Uri

data class OAuthConfig(
    val openAiClientCredentials: Credentials,
    val scope: String = "",
    val redirectionUris: List<Uri> = listOf(Uri.of("https://chat.openai.com/aip/plugin-some_plugin_id/oauth/callback")),
    val contentType: ContentType = ContentType.APPLICATION_JSON.withNoDirectives()
)
