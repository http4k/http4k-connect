package addressbook.local

import addressbook.local.LocalPluginSettings.EMAIL
import addressbook.local.LocalPluginSettings.PLUGIN_BASE_URL
import addressbook.shared.GetAllUsers
import addressbook.shared.GetAnAddress
import addressbook.shared.UserDirectory
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.auth.noauth.NoAuth
import org.http4k.connect.openai.info
import org.http4k.connect.openai.openAiPlugin
import org.http4k.routing.RoutingHttpHandler

/**
 * A service-level plugin operates by authing the calling service only.
 */
fun LocalPlugin(env: Environment = ENV): RoutingHttpHandler {
    val userDirectory = UserDirectory()
    return openAiPlugin(
        info(
            apiVersion = "1.0",
            humanDescription = "localplugin" to "A plugin which uses no auth",
            pluginUrl = PLUGIN_BASE_URL(env),
            contactEmail = EMAIL(env),
        ),
        NoAuth,
        GetAnAddress(userDirectory),
        GetAllUsers(userDirectory)
    )
}
