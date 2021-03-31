package org.http4k.connect.amazon.secretsmanager.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.VersionStage
import se.ansman.kotshi.JsonSerializable
import java.util.UUID

@Http4kConnectAction
@JsonSerializable
data class PutSecretValue internal constructor(
    val SecretId: SecretId,
    val ClientRequestToken: UUID,
    val SecretString: String? = null,
    val SecretBinary: Base64Blob? = null,
    val VersionStages: List<VersionStage>? = null) : SecretsManagerAction<UpdatedSecretValue>(UpdatedSecretValue::class) {
    constructor(
        SecretId: SecretId,
        ClientRequestToken: UUID,
        SecretString: String,
        VersionStages: List<VersionStage>? = null
    ) : this(SecretId, ClientRequestToken, SecretString, null, VersionStages)

    constructor(
        SecretId: SecretId,
        ClientRequestToken: UUID,
        SecretBinary: Base64Blob,
        VersionStages: List<VersionStage>? = null
    ) : this(SecretId, ClientRequestToken, null, SecretBinary, VersionStages)
}
