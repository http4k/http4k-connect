package org.http4k.connect.amazon.core.model

import org.http4k.aws.AwsCredentials
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.useLines

data class AwsProfile(
    val name: ProfileName,
    val accessKeyId: AccessKeyId?,
    val secretAccessKey: SecretAccessKey?,
    val sessionToken: SessionToken?,
    val roleArn: ARN?,
    val sourceProfileName: ProfileName?,
    val roleSessionName: RoleSessionName?,
    val region: Region?
) {
    fun getCredentials(): AwsCredentials? {
        return AwsCredentials(
            accessKey = accessKeyId?.value ?: return null,
            secretKey = secretAccessKey?.value ?: return null,
            sessionToken = sessionToken?.value
        )
    }

    companion object {
        fun loadProfiles(path: Path): Map<ProfileName, AwsProfile> {
            if (!Files.exists(path)) return emptyMap()

            var name = ProfileName.of("default")

            return buildMap {
                fun Map<String, String>.consumeProfile(profileName: ProfileName) {
                    val value: (ProfileName) -> AwsProfile = ::toProfile
                    if (isNotEmpty()) put(profileName, value(profileName))
                }

                val section = mutableMapOf<String, String>()

                path.useLines { lines ->
                    for (line in lines.map(String::trim)) {
                        when {
                            line.startsWith('[') -> {
                                section.consumeProfile(name)
                                section.clear()
                                name = ProfileName.parse(line.trim('[', ']'))
                            }

                            "=" in line -> {
                                val (key, value) = line.split("=", limit = 2).map(String::trim)
                                section[key] = value
                            }
                        }
                    }
                }

                section.consumeProfile(name)
            }
        }

    }
}

private fun Map<String, String>.toProfile(name: ProfileName) = AwsProfile(
    name = name,
    accessKeyId = this["aws_access_key_id"]?.let { AccessKeyId.of(it) },
    secretAccessKey = this["aws_secret_access_key"]?.let { SecretAccessKey.of(it) },
    sessionToken = this["aws_session_token"]?.let { SessionToken.of(it) },
    roleArn = this["role_arn"]?.let { ARN.of(it) },
    sourceProfileName = this["source_profile"]?.let { ProfileName.of(it) },
    roleSessionName = this["role_session_name"]?.let { RoleSessionName.of(it) },
    region = this["region"]?.let { Region.of(it) }
)
