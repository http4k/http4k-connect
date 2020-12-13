package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage
import java.util.UUID

class CreateSecret private constructor(
    val Name: String,
    val ClientRequestToken: UUID,
    val SecretString: String?,
    val SecretBinary: Base64Blob?,
    val Description: String?,
    val KmsKeyId: String?,
    val Tags: List<Tag>?
) : SecretsManagerAction<CreatedSecret>(CreatedSecret::class) {
    constructor(Name: String,
                ClientRequestToken: UUID,
                SecretString: String,
                Description: String? = null,
                KmsKeyId: String? = null,
                Tags: List<Tag>? = null) : this(Name, ClientRequestToken, SecretString, null, Description, KmsKeyId, Tags)

    constructor(Name: String,
                ClientRequestToken: UUID,
                SecretBinary: Base64Blob,
                Description: String? = null,
                KmsKeyId: String? = null,
                Tags: List<Tag>? = null) : this(Name, ClientRequestToken, null, SecretBinary, Description, KmsKeyId, Tags)
}

data class CreatedSecret(
    val ARN: ARN,
    val Name: String,
    val VersionId: VersionId? = null
)

data class DeleteSecret(
    val SecretId: SecretId,
    val ForceDeleteWithoutRecovery: Boolean = false,
    val RecoveryWindowInDays: Int? = null
) : SecretsManagerAction<DeletedSecret>(DeletedSecret::class)

data class DeletedSecret(
    val Name: String,
    val ARN: ARN,
    val DeletionDate: Timestamp
)

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

data class Filters(val Key: String, val Values: List<String>)

enum class SortOrder { asc, desc }

data class ListSecrets(
    val MaxResults: Int? = null,
    val NextToken: String? = null,
    val SortOrder: SortOrder? = null,
    val Filters: List<Filters>? = null
) : SecretsManagerAction<Secrets>(Secrets::class)

data class RotationRules(val AutomaticallyAfterDays: Int? = null)

data class Secret(
    val ARN: ARN? = null,
    val Name: String? = null,
    val CreatedDate: Timestamp? = null,
    val DeletedDate: Timestamp? = null,
    val Description: String? = null,
    val KmsKeyId: KmsKeyId? = null,
    val LastAccessedDate: Timestamp? = null,
    val LastChangedDate: Timestamp? = null,
    val LastRotatedDate: Timestamp? = null,
    val OwningService: String? = null,
    val RotationEnabled: Boolean? = null,
    val RotationLambdaARN: ARN? = null,
    val RotationRules: RotationRules? = null
)

data class Secrets(
    val SecretList: List<Secret>,
    val NextToken: String? = null
)

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

data class UpdatedSecretValue(
    val ARN: ARN,
    val Name: String,
    val VersionId: VersionId? = null,
    val VersionStages: List<String>? = null
)

class UpdateSecret private constructor(
    val SecretId: SecretId,
    val ClientRequestToken: UUID,
    val SecretString: String?,
    val SecretBinary: Base64Blob?,
    val Description: String?,
    val KmsKeyId: String?
): SecretsManagerAction<UpdatedSecret>(UpdatedSecret::class) {
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

data class UpdatedSecret(
    val ARN: ARN,
    val Name: String,
    val VersionId: VersionId? = null
)
