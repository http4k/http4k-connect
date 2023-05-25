package myplugin.oauth

import myplugin.shared.credentials
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.openai.model.Email
import org.http4k.connect.openai.model.VerificationToken
import org.http4k.lens.int
import org.http4k.lens.of
import org.http4k.lens.uri
import org.http4k.lens.value

/**
 * Defines the settings which should exist in the Environment at runtime
 */
object OAuthPluginSettings {
    val PORT by EnvironmentKey.int().of().required()
    val PLUGIN_BASE_URL by EnvironmentKey.uri().of().required()
    val EMAIL by EnvironmentKey.value(Email).of().required()
    val OPEN_AI_CLIENT_CREDENTIALS by EnvironmentKey.credentials().of().required()
    val OPENAI_VERIFICATION_TOKEN by EnvironmentKey.value(VerificationToken).of().required()
    val COOKIE_DOMAIN by EnvironmentKey.of().required()
}
