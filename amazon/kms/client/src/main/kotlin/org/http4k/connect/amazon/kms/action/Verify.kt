package org.http4k.connect.amazon.kms.action

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.SigningAlgorithm

data class Verify(
    val KeyId: KmsKeyId,
    val Message: Base64Blob,
    val Signature: Base64Blob,
    val SigningAlgorithm: SigningAlgorithm,
    val MessageType: String? = null,
    val GrantTokens: List<String>? = null
) : KMSAction<VerifyResult>(VerifyResult::class)

data class VerifyResult(
    val KeyId: KmsKeyId,
    val SignatureValid: Boolean,
    val SigningAlgorithm: SigningAlgorithm
)
