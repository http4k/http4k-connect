package addressbook.oauth.env


import addressbook.oauth.OAuthPlugin
import addressbook.oauth.OAuthPluginSettings.COOKIE_DOMAIN
import addressbook.oauth.OAuthPluginSettings.EMAIL
import addressbook.oauth.OAuthPluginSettings.OPENAI_CLIENT_CREDENTIALS
import addressbook.oauth.OAuthPluginSettings.OPENAI_PLUGIN_ID
import addressbook.oauth.OAuthPluginSettings.OPENAI_VERIFICATION_TOKEN
import addressbook.oauth.OAuthPluginSettings.PLUGIN_BASE_URL
import addressbook.oauth.OAuthPluginSettings.PORT
import addressbook.oauth.OAuthPluginSettings.REDIRECTION_URLS
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.auth.oauth.PluginId
import org.http4k.connect.openai.auth.oauth.openaiPlugin
import org.http4k.connect.openai.model.Email
import org.http4k.connect.openai.model.VerificationToken
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
    val env = ENV.with(
        PORT of 8000,
        OPENAI_PLUGIN_ID of PluginId.of("myplugin"),
        EMAIL of Email.of("foo@bar.com"),
        COOKIE_DOMAIN of "localhost",
        PLUGIN_BASE_URL of Uri.of("http://localhost:8000"),
        OPENAI_CLIENT_CREDENTIALS of Credentials("foo", "bar"),
        OPENAI_VERIFICATION_TOKEN of VerificationToken.of("supersecret"),
        REDIRECTION_URLS of listOf(Uri.of("http://localhost:9000/aip/plugin-myplugin/oauth/callback"))
    )

    FakeOpenAI(
        OPENAI_PLUGIN_ID(env),
        OAuthProvider.openaiPlugin(PLUGIN_BASE_URL(env), OPENAI_CLIENT_CREDENTIALS(env))
    )
        .asServer(SunHttp(9000)).start()

    Cors(UnsafeGlobalPermissive)
        .then(OAuthPlugin(env))
        .asServer(SunHttp(PORT(env)))
        .start()

    println("Login to http://localhost:9000")
}
