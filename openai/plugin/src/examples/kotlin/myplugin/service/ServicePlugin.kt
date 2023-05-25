package myplugin.service

import myplugin.service.ServicePluginSettings.EMAIL
import myplugin.service.ServicePluginSettings.PLUGIN_BASE_URL
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.auth.NoAuth
import org.http4k.connect.openai.info
import org.http4k.connect.openai.openAiPlugin

/**
 * Main creation pattern for an OpenAI plugin
 */
fun ServicePlugin(env: Environment = ENV) = openAiPlugin(
    info(
        apiVersion = "1.0",
        humanDescription = "simpleplugin" to "A plugin which uses service-level auth",
        pluginUrl = PLUGIN_BASE_URL(env),
        contactEmail = EMAIL(env),
    ),
    NoAuth,
    greetingEndpoint()
)
