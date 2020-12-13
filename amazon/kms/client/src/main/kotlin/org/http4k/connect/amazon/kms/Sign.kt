package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.SigningAlgorithm

data class Sign(
    val KeyId: KmsKeyId,
    val Message: Base64Blob,
    val SigningAlgorithm: SigningAlgorithm,
    val Request: List<String>? = null,
    val MessageType: String? = null
) : KMSAction<Signed>(Signed::class)

data class Signed(
    val KeyId: KmsKeyId,
    val Signature: Base64Blob,
    val SigningAlgorithm: SigningAlgorithm
)
