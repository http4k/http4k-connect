package addressbook.user


import addressbook.shared.UserDirectory
import addressbook.user.UserPluginSettings.EMAIL
import addressbook.user.UserPluginSettings.PLUGIN_BASE_URL
import addressbook.user.UserPluginSettings.PORT
import org.http4k.chaos.start
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.FakeOpenAI
import org.http4k.connect.openai.auth.OpenAIPluginId
import org.http4k.connect.openai.model.Email
import org.http4k.connect.openai.plugins.UserPluginIntegrationBuilder
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.ServerFilters
import org.http4k.filter.ServerFilters.Cors
import org.http4k.filter.debug
import org.http4k.server.SunHttp
import org.http4k.server.asServer

/**
 * This program runs a Plugin and Fake OpenAI server. Run it and browse to the local port
 * to demonstrate the login flow.
 */
fun main() {
    val commonEnv = ENV.with(
        PLUGIN_BASE_URL of Uri.of("http://localhost:8000")
    )

    val fakeOpenAi = FakeOpenAI(
        plugins = arrayOf(
            UserPluginIntegrationBuilder(
                ServerFilters.BasicAuth("") { UserDirectory().auth(it) != null },
                OpenAIPluginId.of("userplugin"),
                PLUGIN_BASE_URL(commonEnv)
            )
        )
    ).start()

    val env = commonEnv.with(
        PORT of 8000,
        EMAIL of Email.of("foo@bar.com"),
    )

    Cors(UnsafeGlobalPermissive)
        .then(UserPlugin(env))
        .debug()
        .asServer(SunHttp(PORT(env)))
        .start()

    println("Login to http://localhost:${fakeOpenAi.port()}")
}
