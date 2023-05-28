package addressbook.service


import addressbook.service.ServicePluginSettings.OPENAI_API_KEY
import addressbook.service.ServicePluginSettings.OPENAI_VERIFICATION_TOKEN
import addressbook.user.UserPluginSettings.EMAIL
import addressbook.user.UserPluginSettings.PLUGIN_BASE_URL
import addressbook.user.UserPluginSettings.PORT
import org.http4k.chaos.start
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.FakeOpenAI
import org.http4k.connect.openai.auth.OpenAIPluginId
import org.http4k.connect.openai.model.Email
import org.http4k.connect.openai.model.VerificationToken
import org.http4k.connect.openai.plugins.ServicePluginIntegrationBuilder
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters.BearerAuth
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.ServerFilters.Cors
import org.http4k.security.AccessToken
import org.http4k.server.SunHttp
import org.http4k.server.asServer

/**
 * This program runs a Plugin and Fake OpenAI server. Run it and browse to the local port
 * to demonstrate the login flow.
 */
fun main() {
    val commonEnv = ENV.with(
        PLUGIN_BASE_URL of Uri.of("http://localhost:8000"),
        OPENAI_API_KEY of AccessToken("foobar")
    )

    val fakeOpenAi = FakeOpenAI(
        plugins = arrayOf(
            ServicePluginIntegrationBuilder(
                BearerAuth(OPENAI_API_KEY(commonEnv).value),
                OpenAIPluginId.of("serviceplugin"),
                PLUGIN_BASE_URL(commonEnv)
            )
        )
    ).start()

    val env = commonEnv.with(
        PORT of 8000,
        EMAIL of Email.of("foo@bar.com"),
        OPENAI_VERIFICATION_TOKEN of VerificationToken.of("barfoo")
    )

    Cors(UnsafeGlobalPermissive)
        .then(ServicePlugin(env))
        .asServer(SunHttp(PORT(env)))
        .start()

    println("Login to http://localhost:${fakeOpenAi.port()}")
}
