package org.http4k.connect.amazon.secretsmanager.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.Timestamp

@Http4kConnectAction
data class ListSecrets(
    val MaxResults: Int? = null,
    val NextToken: String? = null,
    val SortOrder: SortOrder? = null,
    val Filters: List<Filters>? = null
) : SecretsManagerAction<Secrets>(Secrets::class)

data class Filters(val Key: String, val Values: List<String>)

enum class SortOrder { asc, desc }

data class RotationRules(val AutomaticallyAfterDays: Int? = null)

data class Secret(
    val ARN: ARN? = null,
    val Name: String? = null,
    val CreatedDate: Timestamp? = null,
    val DeletedDate: Timestamp? = null,
    val Description: String? = null,
    val KmsKeyId: KMSKeyId? = null,
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
