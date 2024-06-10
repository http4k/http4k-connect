package addressbook.oauth

import addressbook.oauth.OAuthPluginSettings.PORT
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.server.SunHttp
import org.http4k.server.asServer

/**
 * Binds the Plugin to a server and starts it as a JVM app
 */
fun OAuthPluginServer(env: Environment = ENV) = OAuthPlugin(env).asServer(SunHttp(PORT(env)))

fun main() {
    OAuthPluginServer().start()
}
