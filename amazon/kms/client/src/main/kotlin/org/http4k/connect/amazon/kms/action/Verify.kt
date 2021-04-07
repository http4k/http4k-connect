package org.http4k.connect.amazon.kms.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.kms.model.SigningAlgorithm
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KMSKeyId
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class Verify(
    val KeyId: KMSKeyId,
    val Message: Base64Blob,
    val Signature: Base64Blob,
    val SigningAlgorithm: SigningAlgorithm,
    val MessageType: String? = null,
    val GrantTokens: List<String>? = null
) : KMSAction<VerifyResult>(VerifyResult::class)

@JsonSerializable
data class VerifyResult(
    val KeyId: KMSKeyId,
    val SignatureValid: Boolean,
    val SigningAlgorithm: SigningAlgorithm
)
