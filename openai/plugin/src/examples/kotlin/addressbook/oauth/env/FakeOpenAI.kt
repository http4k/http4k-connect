package addressbook.oauth.env

import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.BearerAuth
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
    pluginOAuthconfig: OAuthProviderConfig,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC()
): HttpHandler {
    val oAuthPersistence = InsecureCookieBasedOAuthPersistence("openai", Duration.ofSeconds(60), clock)
    val oAuthProvider = OAuthProvider(
        pluginOAuthconfig,
        http,
        Uri.of("http://localhost:9000/oauth2/callback"),
        emptyList(),
        oAuthPersistence
    )

    return routes(
        "/oauth2/callback" bind GET to oAuthProvider.callback,
        "/" bind GET to oAuthProvider.authFilter.then {
            val openAiServer = BearerAuth(oAuthPersistence.retrieveToken(it)!!.value)
                .then(http)
            openAiServer(Request(GET, pluginOAuthconfig.apiBase.path("/address")))
        })
}
