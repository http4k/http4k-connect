package addressbook.oauth


import addressbook.oauth.OAuthPluginSettings.COOKIE_DOMAIN
import addressbook.oauth.OAuthPluginSettings.EMAIL
import addressbook.oauth.OAuthPluginSettings.OPENAI_CLIENT_CREDENTIALS
import addressbook.oauth.OAuthPluginSettings.OPENAI_PLUGIN_ID
import addressbook.oauth.OAuthPluginSettings.OPENAI_VERIFICATION_TOKEN
import addressbook.oauth.OAuthPluginSettings.PLUGIN_BASE_URL
import addressbook.oauth.OAuthPluginSettings.PORT
import addressbook.oauth.OAuthPluginSettings.REDIRECTION_URLS
import org.http4k.chaos.start
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.FakeOpenAI
import org.http4k.connect.openai.auth.OpenAIPluginId
import org.http4k.connect.openai.auth.oauth.openaiPlugin
import org.http4k.connect.openai.model.Email
import org.http4k.connect.openai.model.VerificationToken
import org.http4k.connect.openai.plugins.OAuthPluginIntegrationBuilder
import org.http4k.core.Credentials
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.ServerFilters.Cors
import org.http4k.security.OAuthProvider
import org.http4k.server.SunHttp
import org.http4k.server.asServer

/**
 * This program runs a Plugin and Fake OpenAI server. Run it and browse to the local port
 * to demonstrate the login flow.
 */
fun main() {
    val commonEnv = ENV.with(
        OPENAI_PLUGIN_ID of OpenAIPluginId.of("myplugin"),
        PLUGIN_BASE_URL of Uri.of("http://localhost:8000"),
        OPENAI_CLIENT_CREDENTIALS of Credentials("foo", "bar"),
    )

    val fakeOpenAi = FakeOpenAI(
        plugins = arrayOf(
            OAuthPluginIntegrationBuilder(
                OPENAI_PLUGIN_ID(commonEnv),
                OAuthProvider.openaiPlugin(PLUGIN_BASE_URL(commonEnv), OPENAI_CLIENT_CREDENTIALS(commonEnv)),
            )
        )
    ).start()

    val env = commonEnv.with(
        PORT of 8000,
        EMAIL of Email.of("foo@bar.com"),
        COOKIE_DOMAIN of "localhost",
        OPENAI_VERIFICATION_TOKEN of VerificationToken.of("supersecret"),
        REDIRECTION_URLS of listOf(
            Uri.of("http://localhost:${fakeOpenAi.port()}/aip/plugin-${OPENAI_PLUGIN_ID(commonEnv)}/oauth/callback")
        )
    )

    Cors(UnsafeGlobalPermissive)
        .then(OAuthPlugin(env))
        .asServer(SunHttp(PORT(env)))
        .start()

    println("Login to http://localhost:${fakeOpenAi.port()}")
}
