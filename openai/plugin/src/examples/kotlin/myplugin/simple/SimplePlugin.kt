package myplugin.simple

import myplugin.simple.SimplePluginSettings.EMAIL
import myplugin.simple.SimplePluginSettings.PLUGIN_BASE_URL
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.openai.auth.NoAuth
import org.http4k.connect.openai.info
import org.http4k.connect.openai.openAiPlugin

/**
 * Main creation pattern for an OpenAI plugin
 */
fun SimplePlugin(env: Environment) = openAiPlugin(
    info(
        apiVersion = "1.0",
        humanDescription = "simpleplugin" to "my simple plugin",
        pluginUrl = PLUGIN_BASE_URL(env),
        contactEmail = EMAIL(env),
    ),
    NoAuth,
    greetingEndpoint()
)
