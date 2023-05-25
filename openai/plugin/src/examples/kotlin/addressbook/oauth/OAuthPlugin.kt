package addressbook.oauth

import addressbook.oauth.OAuthPluginSettings.COOKIE_DOMAIN
import addressbook.oauth.OAuthPluginSettings.EMAIL
import addressbook.oauth.OAuthPluginSettings.OPENAI_VERIFICATION_TOKEN
import addressbook.oauth.OAuthPluginSettings.OPEN_AI_CLIENT_CREDENTIALS
import addressbook.oauth.OAuthPluginSettings.PLUGIN_BASE_URL
import addressbook.shared.GetAllUsers
import addressbook.shared.GetAnAddress
import addressbook.shared.GetMyAddress
import addressbook.shared.UserDirectory
import addressbook.shared.UserId
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.auth.OAuth
import org.http4k.connect.openai.auth.OAuthConfig
import org.http4k.connect.openai.auth.oauth.SecureStrings
import org.http4k.connect.openai.auth.oauth.SecureStrings.Companion.Random
import org.http4k.connect.openai.auth.oauth.impl.StorageOAuthMachinery
import org.http4k.connect.openai.auth.oauth.impl.StorageProvider
import org.http4k.connect.openai.info
import org.http4k.connect.openai.model.AuthedSystem.Companion.openai
import org.http4k.connect.openai.openAiPlugin
import org.http4k.core.RequestContexts
import org.http4k.lens.RequestContextKey
import org.http4k.routing.RoutingHttpHandler
import java.time.Clock
import java.time.Clock.systemUTC
import java.time.Duration.ofMinutes

/**
 * Main creation pattern for an OpenAI plugin
 */
fun OAuthPlugin(
    env: Environment = ENV,
    clock: Clock = systemUTC(),
    strings: SecureStrings = Random(),
    storageProvider: StorageProvider = InMemoryStorageProvider,
): RoutingHttpHandler {
    val userDirectory = UserDirectory()
    val contexts = RequestContexts()
    val userPrincipal = RequestContextKey.required<UserId>(contexts)

    val machinery = StorageOAuthMachinery<UserId>(storageProvider, strings, ofMinutes(1), COOKIE_DOMAIN(env), clock)

    return openAiPlugin(
        info(
            apiVersion = "1.0",
            humanDescription = "oauthplugin" to "A plugin which uses oauth",
            pluginUrl = PLUGIN_BASE_URL(env),
            contactEmail = EMAIL(env),
        ),
        OAuth(
            PLUGIN_BASE_URL(env),
            OAuthConfig(OPEN_AI_CLIENT_CREDENTIALS(env)),
            machinery,
            userPrincipal,
            UserIdAuthChallenge(userDirectory),
            mapOf(openai to OPENAI_VERIFICATION_TOKEN(env)),
            clock
        ),
        GetMyAddress(userDirectory, userPrincipal),
        GetAnAddress(userDirectory),
        GetAllUsers(userDirectory)
    )
}

