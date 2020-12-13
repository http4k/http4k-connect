package org.http4k.connect.amazon.kms.action

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.EncryptionAlgorithm
import org.http4k.connect.amazon.model.KmsKeyId

data class Decrypt(
    val KeyId: KmsKeyId,
    val CiphertextBlob: Base64Blob,
    val EncryptionAlgorithm: EncryptionAlgorithm? = null,
    val EncryptionContext: Map<String, String>? = null,
    val GrantTokens: List<String>? = null
) : KMSAction<Decrypted>(Decrypted::class)

data class Decrypted(val KeyId: KmsKeyId, val Plaintext: Base64Blob, val EncryptionAlgorithm: EncryptionAlgorithm)
