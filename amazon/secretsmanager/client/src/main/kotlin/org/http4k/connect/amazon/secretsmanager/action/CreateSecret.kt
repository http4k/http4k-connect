package org.http4k.connect.amazon.secretsmanager.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.VersionId
import se.ansman.kotshi.JsonSerializable
import java.util.UUID

@Http4kConnectAction
@JsonSerializable
data class CreateSecret internal constructor(
    val Name: String,
    val ClientRequestToken: UUID,
    val SecretString: String?,
    val SecretBinary: Base64Blob?,
    val Description: String?,
    val KmsKeyId: KMSKeyId?,
    val Tags: List<Tag>?
) : SecretsManagerAction<CreatedSecret>(CreatedSecret::class) {
    constructor(Name: String,
                ClientRequestToken: UUID,
                SecretString: String,
                Description: String? = null,
                KmsKeyId: KMSKeyId? = null,
                Tags: List<Tag>? = null) : this(Name, ClientRequestToken, SecretString, null, Description, KmsKeyId, Tags)

    constructor(Name: String,
                ClientRequestToken: UUID,
                SecretBinary: Base64Blob,
                Description: String? = null,
                KmsKeyId: KMSKeyId? = null,
                Tags: List<Tag>? = null) : this(Name, ClientRequestToken, null, SecretBinary, Description, KmsKeyId, Tags)
}

@JsonSerializable
data class CreatedSecret(
    val ARN: ARN,
    val Name: String,
    val VersionId: VersionId? = null
)
