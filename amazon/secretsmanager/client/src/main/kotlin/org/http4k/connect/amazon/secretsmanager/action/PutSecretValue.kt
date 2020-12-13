package org.http4k.connect.amazon.secretsmanager.action

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.SecretId
import java.util.UUID

class PutSecretValue private constructor(
    val SecretId: SecretId,
    val ClientRequestToken: UUID,
    val SecretString: String?,
    val SecretBinary: Base64Blob?,
    val VersionStages: List<String>?
) : SecretsManagerAction<UpdatedSecretValue>(UpdatedSecretValue::class) {
    constructor(SecretId: SecretId,
                ClientRequestToken: UUID,
                SecretString: String,
                VersionStages: List<String>? = null) : this(SecretId, ClientRequestToken, SecretString, null, VersionStages)

    constructor(SecretId: SecretId,
                ClientRequestToken: UUID,
                SecretBinary: Base64Blob,
                VersionStages: List<String>? = null) : this(SecretId, ClientRequestToken, null, SecretBinary, VersionStages)
}
