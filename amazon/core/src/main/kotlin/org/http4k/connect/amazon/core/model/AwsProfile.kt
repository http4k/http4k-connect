package org.http4k.connect.amazon.core.model

import org.http4k.aws.AwsCredentials
import java.nio.file.Path
import kotlin.io.path.Path

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
        fun loadProfiles(path: Path) = loadProfiles(path) { map, name ->
            AwsProfile(
                name = name,
                accessKeyId = map["aws_access_key_id"]?.let { AccessKeyId.of(it) },
                secretAccessKey = map["aws_secret_access_key"]?.let { SecretAccessKey.of(it) },
                sessionToken = map["aws_session_token"]?.let { SessionToken.of(it) },
                roleArn = map["role_arn"]?.let { ARN.of(it) },
                sourceProfileName = map["source_profile"]?.let { ProfileName.of(it) },
                roleSessionName = map["role_session_name"]?.let { RoleSessionName.of(it) },
                region = map["region"]?.let { Region.of(it) }
            )
        }
    }
}

fun main() {
    println(AwsProfile.loadProfiles(Path(System.getProperty("user.home")).resolve(".aws/credentials")))
}
