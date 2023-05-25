package addressbook.oauth

import addressbook.oauth.OAuthPluginSettings.COOKIE_DOMAIN
import addressbook.oauth.OAuthPluginSettings.EMAIL
import addressbook.oauth.OAuthPluginSettings.OPENAI_VERIFICATION_TOKEN
import addressbook.oauth.OAuthPluginSettings.OPEN_AI_CLIENT_CREDENTIALS
import addressbook.oauth.OAuthPluginSettings.PLUGIN_BASE_URL
import addressbook.shared.GetAnAddress
import addressbook.shared.UserDirectory
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.connect.openai.auth.OAuth
import org.http4k.connect.openai.auth.oauth.SecureStrings
import org.http4k.connect.openai.auth.oauth.SecureStrings.Companion.Random
import org.http4k.connect.openai.auth.oauth.StorageOAuthMachinery
import org.http4k.connect.openai.auth.oauth.StorageProvider
import org.http4k.connect.openai.info
import org.http4k.connect.openai.model.AuthedSystem.Companion.openai
import org.http4k.connect.openai.openAiPlugin
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
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
    storageProvider: StorageProvider = InMemoryStorageProvider
) = openAiPlugin(
    info(
        apiVersion = "1.0",
        humanDescription = "oauthplugin" to "A plugin which uses oauth",
        pluginUrl = PLUGIN_BASE_URL(env),
        contactEmail = EMAIL(env),
    ),
    OAuth(
        PLUGIN_BASE_URL(env),
        mapOf(openai to OPENAI_VERIFICATION_TOKEN(env)),
        OPEN_AI_CLIENT_CREDENTIALS(env),
        StorageOAuthMachinery(storageProvider, strings, ofMinutes(1), COOKIE_DOMAIN(env), clock),
        clock,
        ""
    ),
    GetAnAddress(UserDirectory())
)

object InMemoryStorageProvider : StorageProvider {
    override fun <T : Any> invoke() = Storage.InMemory<T>()
}
