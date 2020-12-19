package org.http4k.connect.amazon.secretsmanager.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage

@Http4kConnectAction
data class GetSecretValue(
    val SecretId: SecretId,
    val VersionId: VersionId? = null,
    val VersionStage: VersionStage? = null
) : SecretsManagerAction<SecretValue>(SecretValue::class)

data class SecretValue(
    val ARN: ARN,
    val CreatedDate: Timestamp,
    val Name: String,
    val SecretBinary: Base64Blob?,
    val SecretString: String?,
    val VersionId: VersionId,
    val VersionStages: List<VersionStage>
)
