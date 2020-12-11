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

class CreateSecretRequest private constructor(
    val Name: String,
    val ClientRequestToken: UUID,
    val SecretString: String?,
    val SecretBinary: Base64Blob?,
    val Description: String?,
    val KmsKeyId: String?,
    val Tags: List<Tag>?
) {
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

data class CreateSecretResponse(
    val ARN: ARN,
    val Name: String,
    val VersionId: VersionId? = null
)

data class DeleteSecretRequest(
    val SecretId: SecretId,
    val ForceDeleteWithoutRecovery: Boolean = false,
    val RecoveryWindowInDays: Int? = null
)

data class DeleteSecretResponse(
    val Name: String,
    val ARN: ARN,
    val DeletionDate: Timestamp
)


data class GetSecretValueRequest(
    val SecretId: SecretId,
    val VersionId: VersionId? = null,
    val VersionStage: VersionStage? = null
)

data class GetSecretValueResponse(
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

data class ListSecretsRequest(
    val MaxResults: Int? = null,
    val NextToken: String? = null,
    val SortOrder: SortOrder? = null,
    val Filters: List<Filters>? = null
)

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

data class ListSecretsResponse(
    val SecretList: List<Secret>,
    val NextToken: String? = null
)

class PutSecretValueRequest private constructor(
    val SecretId: SecretId,
    val ClientRequestToken: UUID,
    val SecretString: String?,
    val SecretBinary: Base64Blob?,
    val VersionStages: List<String>?
) {
    constructor(SecretId: SecretId,
                ClientRequestToken: UUID,
                SecretString: String,
                VersionStages: List<String>? = null) : this(SecretId, ClientRequestToken, SecretString, null, VersionStages)

    constructor(SecretId: SecretId,
                ClientRequestToken: UUID,
                SecretBinary: Base64Blob,
                VersionStages: List<String>? = null) : this(SecretId, ClientRequestToken, null, SecretBinary, VersionStages)
}

data class PutSecretValueResponse(
    val ARN: ARN,
    val Name: String,
    val VersionId: VersionId? = null,
    val VersionStages: List<String>? = null
)

class UpdateSecretRequest private constructor(
    val SecretId: SecretId,
    val ClientRequestToken: UUID,
    val SecretString: String?,
    val SecretBinary: Base64Blob?,
    val Description: String?,
    val KmsKeyId: String?
) {
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

data class UpdateSecretResponse(
    val ARN: ARN,
    val Name: String,
    val VersionId: VersionId? = null
)
