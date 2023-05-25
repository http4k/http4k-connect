package myplugin.service

import myplugin.service.ServicePluginSettings.EMAIL
import myplugin.service.ServicePluginSettings.OPENAI_API_KEY
import myplugin.service.ServicePluginSettings.OPENAI_VERIFICATION_TOKEN
import myplugin.service.ServicePluginSettings.PLUGIN_BASE_URL
import myplugin.shared.UserDirectory
import myplugin.user.GetAnAddress
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.auth.AuthToken.Bearer
import org.http4k.connect.openai.auth.ServiceLevelAuth
import org.http4k.connect.openai.info
import org.http4k.connect.openai.model.AuthedSystem.Companion.openai
import org.http4k.connect.openai.openAiPlugin

/**
 * Main creation pattern for an OpenAI plugin
 */
fun ServicePlugin(env: Environment = ENV) = openAiPlugin(
    info(
        apiVersion = "1.0",
        humanDescription = "serviceplugin" to "A plugin which uses service-level auth",
        pluginUrl = PLUGIN_BASE_URL(env),
        contactEmail = EMAIL(env),
    ),
    ServiceLevelAuth(
        Bearer { it == OPENAI_API_KEY(env) },
        mapOf(openai to OPENAI_VERIFICATION_TOKEN(env))
    ),
    GetAnAddress(UserDirectory())
)
