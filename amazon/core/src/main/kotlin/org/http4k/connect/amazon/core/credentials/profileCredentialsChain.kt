package org.http4k.connect.amazon.core.credentials

import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_CREDENTIAL_PROFILES_FILE
import org.http4k.connect.amazon.AWS_PROFILE
import org.http4k.connect.amazon.core.model.ProfileName
import org.http4k.connect.amazon.defaultCredentialsProfilesFile
import org.ini4j.Ini
import java.nio.file.Files
import java.nio.file.Path

fun CredentialsChain.Companion.Profile(env: Environment) = CredentialsChain.Profile(
    profileName = AWS_PROFILE(env),
    credentialsPath = AWS_CREDENTIAL_PROFILES_FILE(env)
)

// TODO Support STS AssumeRole
fun CredentialsChain.Companion.Profile(
    profileName: ProfileName,
    credentialsPath: Path = defaultCredentialsProfilesFile
) = CredentialsChain {
    if (!Files.exists(credentialsPath)) return@CredentialsChain null

    val profiles = credentialsPath.toFile().inputStream().use { content ->
        Ini().apply { load(content) }
    }

    val profile = profiles[profileName.value] ?: return@CredentialsChain null

    AwsCredentials(
        accessKey = profile["aws_access_key_id"] ?: return@CredentialsChain null,
        secretKey = profile["aws_secret_access_key"] ?: return@CredentialsChain null,
        sessionToken = profile["aws_session_token"]
    )
}
