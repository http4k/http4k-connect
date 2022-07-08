package org.http4k.connect.amazon.core.model

import org.http4k.aws.AwsCredentials

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

    companion object
}
