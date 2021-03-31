package org.http4k.connect.amazon.secretsmanager.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.Timestamp
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class DeleteSecret(
    val SecretId: SecretId,
    val ForceDeleteWithoutRecovery: Boolean? = null,
    val RecoveryWindowInDays: Int? = null
) : SecretsManagerAction<DeletedSecret>(DeletedSecret::class)

@JsonSerializable
data class DeletedSecret(
    val Name: String,
    val ARN: ARN,
    val DeletionDate: Timestamp
)
