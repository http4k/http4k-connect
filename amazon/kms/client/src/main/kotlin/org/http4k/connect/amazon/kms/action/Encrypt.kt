package org.http4k.connect.amazon.kms.action

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.EncryptionAlgorithm
import org.http4k.connect.amazon.model.KmsKeyId

data class Encrypt(
    val KeyId: KmsKeyId,
    val Plaintext: Base64Blob,
    val EncryptionAlgorithm: EncryptionAlgorithm? = null,
    val EncryptionContext: Map<String, String>? = null,
    val GrantTokens: List<String>? = null
) : KMSAction<Encrypted>(Encrypted::class)

data class Encrypted(val KeyId: KmsKeyId, val CiphertextBlob: Base64Blob, val EncryptionAlgorithm: EncryptionAlgorithm)
