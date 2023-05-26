package addressbook.oauth.env


import addressbook.oauth.OAuthPlugin
import addressbook.oauth.OAuthPluginSettings.COOKIE_DOMAIN
import addressbook.oauth.OAuthPluginSettings.EMAIL
import addressbook.oauth.OAuthPluginSettings.OPENAI_VERIFICATION_TOKEN
import addressbook.oauth.OAuthPluginSettings.OPEN_AI_CLIENT_CREDENTIALS
import addressbook.oauth.OAuthPluginSettings.PLUGIN_BASE_URL
import addressbook.oauth.OAuthPluginSettings.PORT
import addressbook.oauth.OAuthPluginSettings.REDIRECTION_URLS
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.model.Email
import org.http4k.connect.openai.model.VerificationToken
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters.BearerAuth
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.ServerFilters.Cors
import org.http4k.filter.debug
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.security.OAuthProviderConfig
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import java.time.Clock

fun FakeOpenAI(
    pluginUrl: Uri,
    credentials: Credentials,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC()
): HttpHandler {
    val oAuthPersistence = InsecureCookieBasedOAuthPersistence("openao", clock = clock)
    val oAuthProvider = OAuthProvider(
        OAuthProviderConfig(pluginUrl, "/authorize", "/oauth2/token", credentials),
        http,
        Uri.of("http://localhost:9000/oauth2/callback"),
        emptyList(),
        oAuthPersistence
    )

    return routes(
        "/oauth2/callback" bind GET to oAuthProvider.callback,
        "/" bind GET to oAuthProvider.authFilter.then {
            val pluginApi = BearerAuth(oAuthPersistence.retrieveToken(it)!!.value).then(http)
            pluginApi(Request(GET, pluginUrl.path("/address"))) })
}

fun main() {
    val env = ENV.with(
        PORT of 8000,
        EMAIL of Email.of("foo@bar.com"),
        COOKIE_DOMAIN of "localhost",
        PLUGIN_BASE_URL of Uri.of("http://localhost:8000"),
        OPEN_AI_CLIENT_CREDENTIALS of Credentials("foo", "bar"),
        OPENAI_VERIFICATION_TOKEN of VerificationToken.of("supersecret"),
        REDIRECTION_URLS of listOf(Uri.of("http://localhost:9000/oauth2/callback"))
    )

    FakeOpenAI(PLUGIN_BASE_URL(env), OPEN_AI_CLIENT_CREDENTIALS(env)).asServer(SunHttp(9000)).start()

    Cors(UnsafeGlobalPermissive)
        .then(OAuthPlugin(env))
        .debug()
        .asServer(SunHttp(PORT(env)))
        .start()

    println("Login to http://localhost:9000")
}
