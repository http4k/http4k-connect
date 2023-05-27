package addressbook.oauth

import addressbook.oauth.OAuthPluginSettings.COOKIE_DOMAIN
import addressbook.oauth.OAuthPluginSettings.EMAIL
import addressbook.oauth.OAuthPluginSettings.OPENAI_VERIFICATION_TOKEN
import addressbook.oauth.OAuthPluginSettings.OPEN_AI_CLIENT_CREDENTIALS
import addressbook.oauth.OAuthPluginSettings.PLUGIN_BASE_URL
import addressbook.oauth.OAuthPluginSettings.REDIRECTION_URLS
import addressbook.oauth.auth.InMemoryStorageProvider
import addressbook.oauth.auth.LoginAuthenticate
import addressbook.oauth.auth.StorageOAuthMachinery
import addressbook.shared.GetAllUsers
import addressbook.shared.GetAnAddress
import addressbook.shared.GetMyAddress
import addressbook.shared.UserDirectory
import addressbook.shared.UserId
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.auth.oauth.OAuth
import org.http4k.connect.openai.auth.oauth.OAuthConfig
import org.http4k.connect.openai.auth.oauth.SecureStrings
import org.http4k.connect.openai.auth.oauth.SecureStrings.Companion.Random
import org.http4k.connect.openai.info
import org.http4k.connect.openai.model.AuthedSystem.Companion.openai
import org.http4k.connect.openai.openAiPlugin
import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.ServerFilters.InitialiseRequestContext
import org.http4k.lens.RequestContextKey
import org.http4k.routing.RoutingHttpHandler
import java.time.Clock
import java.time.Clock.systemUTC
import java.time.Duration.ofMinutes
import java.time.Duration.ofSeconds

/**
 * A plugin which is protected by an OAuth AuthorizationCode flow (using a custom login screen)
 */
fun OAuthPlugin(
    env: Environment = ENV,
    clock: Clock = systemUTC(),
    strings: SecureStrings = Random(),
): RoutingHttpHandler {
    val contexts = RequestContexts()
    val userPrincipal = RequestContextKey.required<UserId>(contexts)

    val userDirectory = UserDirectory()

    return InitialiseRequestContext(contexts)
        .then(
            openAiPlugin(
                info(
                    apiVersion = "1.0",
                    humanDescription = "oauthplugin" to "A plugin which uses oauth",
                    pluginUrl = PLUGIN_BASE_URL(env),
                    contactEmail = EMAIL(env),
                ),
                OAuth(
                    PLUGIN_BASE_URL(env),
                    OAuthConfig(OPEN_AI_CLIENT_CREDENTIALS(env), redirectionUris = REDIRECTION_URLS(env)),
                    StorageOAuthMachinery(
                        InMemoryStorageProvider, strings, ofMinutes(1), ofSeconds(2), COOKIE_DOMAIN(env), clock,
                        LoginAuthenticate(userDirectory)
                    ),
                    userPrincipal,
                    mapOf(openai to OPENAI_VERIFICATION_TOKEN(env)),
                    clock
                ),
                GetMyAddress(userDirectory, userPrincipal),
                GetAnAddress(userDirectory),
                GetAllUsers(userDirectory)
            )
        )
}

