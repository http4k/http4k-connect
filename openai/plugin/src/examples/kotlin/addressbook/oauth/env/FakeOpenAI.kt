package addressbook.oauth.env

import org.http4k.client.JavaHttpClient
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.OAuthProvider
import org.http4k.security.OAuthProviderConfig
import java.time.Clock

fun FakeOpenAI(
    oauthServerUri: Uri,
    credentials: Credentials,
    oauthServerHttp: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC()
): HttpHandler {
    val oAuthProvider = OAuthProvider(
        OAuthProviderConfig(oauthServerUri, "/authorize", "/oauth2/token", credentials),
        oauthServerHttp,
        Uri.of("http://localhost:9000/oauth2/callback"),
        emptyList(),
        InMemoryOAuthPersistence(clock)
    )

    return routes(
        "/oauth2/callback" bind GET to oAuthProvider.callback,
        "/" bind GET to oAuthProvider.authFilter.then { Response(OK).body("hello world!") })
}
