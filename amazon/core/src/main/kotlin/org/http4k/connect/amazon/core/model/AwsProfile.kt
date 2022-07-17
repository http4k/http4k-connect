package org.http4k.connect.amazon.core.model

import org.http4k.aws.AwsCredentials
import org.ini4j.Ini
import java.nio.file.Files
import java.nio.file.Path

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

            val sections = path.toFile().inputStream().use { content ->
                Ini().apply { load(content) }
            }

            return sections.map { (name, section) ->
                AwsProfile(
                    name = ProfileName.of(name),
                    accessKeyId = section["aws_access_key_id"]?.let { AccessKeyId.of(it) },
                    secretAccessKey = section["aws_secret_access_key"]?.let { SecretAccessKey.of(it) },
                    sessionToken = section["aws_session_token"]?.let { SessionToken.of(it) },
                    roleArn = section["role_arn"]?.let { ARN.of(it) },
                    sourceProfileName = section["source_profile"]?.let { ProfileName.of(it) },
                    roleSessionName = section["role_session_name"]?.let { RoleSessionName.of(it) },
                    region = section["region"]?.let { Region.of(it) }
                )
            }.associateBy { it.name }
        }

    }
}
