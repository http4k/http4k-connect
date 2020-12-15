package org.http4k.connect.amazon.secretsmanager.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.VersionId
import java.util.UUID

@Http4kConnectAction
class UpdateSecret private constructor(
    val SecretId: SecretId,
    val ClientRequestToken: UUID,
    val SecretString: String?,
    val SecretBinary: Base64Blob?,
    val Description: String?,
    val KmsKeyId: String?
) : SecretsManagerAction<UpdatedSecret>(UpdatedSecret::class) {
    constructor(SecretId: SecretId,
                ClientRequestToken: UUID,
                SecretString: String,
                Description: String? = null,
                KmsKeyId: String? = null) : this(SecretId, ClientRequestToken, SecretString, null, Description, KmsKeyId)

    constructor(SecretId: SecretId,
                ClientRequestToken: UUID,
                SecretBinary: Base64Blob,
                Description: String? = null,
                KmsKeyId: String? = null) : this(SecretId, ClientRequestToken, null, SecretBinary, Description, KmsKeyId)
}

data class UpdatedSecretValue(
    val ARN: ARN,
    val Name: String,
    val VersionId: VersionId? = null,
    val VersionStages: List<String>? = null
)

data class UpdatedSecret(
    val ARN: ARN,
    val Name: String,
    val VersionId: VersionId? = null
)
