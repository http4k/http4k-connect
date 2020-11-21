package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage
import java.util.UUID

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
        val SecretBinary: Base64Blob,
        val SecretString: String,
        val VersionId: VersionId,
        val VersionStages: List<VersionStage>
    )
}

object PutSecret {
    data class Request(
        val SecretId: SecretId,
        val SecretBinary: Base64Blob? = null,
        val SecretString: String? = null,
        val ClientRequestToken: UUID? = null,
        val VersionStages: List<VersionStage>? = null
    )

    data class Response(
        val ARN: ARN,
        val Name: String,
        val VersionId: VersionId,
        val VersionStages: List<VersionStage>
    )
}

object UpdateSecret {
    data class Request(
        val SecretId: SecretId,
        val SecretBinary: Base64Blob? = null,
        val SecretString: String? = null,
        val Description: String? = null,
        val KmsKeyId: KmsKeyId? = null,
        val ClientRequestToken: UUID? = null
    )

    data class Response(
        val ARN: ARN,
        val Name: String,
        val VersionId: VersionId
    )
}

object DeleteSecret {
    data class Request(
        val SecretId: SecretId,
        val ForceDeleteWithoutRecovery: Boolean = false,
        val RecoveryWindowInDays: Int? = null
    )

    data class Response(
        val ARN: ARN,
        val DeletionDate: Timestamp,
        val Name: String
    )
}

object CreateSecret {
    data class Request(
        val Name: String,
        val SecretBinary: Base64Blob? = null,
        val SecretString: String? = null,
        val ClientRequestToken: UUID? = null,
        val Description: String? = null,
        val KmsKeyId: String? = null,
        val Tags: Map<String, String>? = null
    )

    data class Response(
        val ARN: ARN,
        val Name: String,
        val VersionId: VersionId
    )
}
