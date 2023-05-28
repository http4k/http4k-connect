package addressbook.oauth.env

import org.http4k.client.JavaHttpClient
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.security.OAuthProviderConfig
import java.time.Clock
import java.time.Duration

/**
 * FakeOpenAI implementation which pretends to be the OpenAI server. It performs the
 * oauth flow to the plugin, obtaining a token and driving the login.
 */
fun FakeOpenAI(
    pluginUrl: Uri,
    credentials: Credentials,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC()
): HttpHandler {
    val oAuthPersistence = InsecureCookieBasedOAuthPersistence("openai", Duration.ofSeconds(60), clock)
    val oAuthProvider = OAuthProvider(
        OAuthProviderConfig(pluginUrl, "/authorize", "/oauth2/token", credentials),
        http,
        Uri.of("http://localhost:9000/oauth2/callback"),
        emptyList(),
        oAuthPersistence
    )

    return routes(
        "/oauth2/callback" bind Method.GET to oAuthProvider.callback,
        "/" bind Method.GET to oAuthProvider.authFilter.then {
            val pluginApi =
                org.http4k.filter.ClientFilters.BearerAuth(oAuthPersistence.retrieveToken(it)!!.value).then(http)
            pluginApi(org.http4k.core.Request(org.http4k.core.Method.GET, pluginUrl.path("/address")))
        })
}
