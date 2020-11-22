package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.Choice2
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage
import java.util.UUID

object CreateSecret {
    class Request(
        val Name: String,
        val ClientRequestToken: UUID,
        secret: Choice2<Base64Blob, String>,
        val Description: String? = null,
        val KmsKeyId: String? = null,
        val Tags: Map<String, String>? = null
    ) {
        val SecretBinary = secret.as1()
        val SecretString = secret.as2()
    }

    data class Response(
        val ARN: ARN,
        val Name: String,
        val VersionId: VersionId? = null
    )
}

object DeleteSecret {
    data class Request(
        val SecretId: SecretId,
        val ForceDeleteWithoutRecovery: Boolean = false,
        val RecoveryWindowInDays: Int? = null
    )

    data class Response(
        val Name: String,
        val ARN: ARN,
        val DeletionDate: Timestamp
    )
}

object GetSecret {
    data class Request(
        val SecretId: SecretId,
        val VersionId: VersionId? = null,
        val VersionStage: VersionStage? = null
    )

    data class Response(
        val ARN: ARN,
        val CreatedDate: Timestamp,
        val Name: String,
        val SecretBinary: Base64Blob?,
        val SecretString: String?,
        val VersionId: VersionId,
        val VersionStages: List<VersionStage>
    )
}

object ListSecrets {
    data class Filters(val Key: String, val Values: List<String>)

    enum class SortOrder { asc, desc }

    data class Request(
        val MaxResults: Int? = null,
        val NextToken: String? = null,
        val SortOrder: SortOrder? = null,
        val Filters: List<Filters>? = null
    )

    data class RotationRules(val AutomaticallyAfterDays: Number? = null)

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
        val RotationRules: RotationRules? = null,
        val SecretVersionsToStages: Map<String, List<String>> = mapOf(),
        val Tags: Map<String, String> = mapOf()
    )

    data class Response(
        val SecretList: List<Secret>,
        val NextToken: String? = null
    )
}

object UpdateSecret {
    class Request(
        val SecretId: SecretId,
        val ClientRequestToken: UUID,
        secret: Choice2<Base64Blob, String>,
        val Description: String? = null,
        val KmsKeyId: KmsKeyId? = null
    ) {
        val SecretBinary = secret.as1()
        val SecretString = secret.as2()
    }

    data class Response(
        val ARN: ARN,
        val Name: String,
        val VersionId: VersionId? = null
    )
}

