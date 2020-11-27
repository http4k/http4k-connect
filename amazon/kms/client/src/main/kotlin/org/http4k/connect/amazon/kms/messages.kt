package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.Timestamp

object CreateKey {
    data class Request(
        val BypassPolicyLockoutSafetyCheck: Boolean? = null,
        val CustomerMasterKeySpec: String? = null,
        val CustomKeyStoreId: String? = null,
        val Description: String? = null,
        val KeyUsage: String? = null,
        val Origin: String? = null,
        val Policy: String? = null,
        val Tags: List<Tag>? = null
    )

    data class Response(val KeyMetadata: KeyMetadata)
}

object DescribeKey {
    data class Request(val KeyId: KmsKeyId, val GrantTokens: List<String>? = null)
    data class Response(val KeyMetadata: KeyMetadata)
}

object Decrypt {
    data class Request(
        val KeyId: KmsKeyId,
        val CiphertextBlob: Base64Blob,
        val EncryptionAlgorithm: String? = null,
        val EncryptionContext: Map<String, String>? = null,
        val GrantTokens: List<String>? = null
    )

    data class Response(val KeyId: KmsKeyId, val Plaintext: Base64Blob, val EncryptionAlgorithm: String)
}

object Encrypt {
    data class Request(
        val KeyId: KmsKeyId,
        val Plaintext: Base64Blob,
        val EncryptionAlgorithm: String? = null,
        val EncryptionContext: Map<String, String>? = null,
        val GrantTokens: List<String>? = null
    )

    data class Response(val KeyId: KmsKeyId, val CiphertextBlob: Base64Blob, val EncryptionAlgorithm: String)
}

object GetPublicKey {
    data class Request(
        val KeyId: KmsKeyId,
        val GrantTokens: List<String>? = null
    )

    data class Response(
        val KeyId: KmsKeyId,
        val CustomerMasterKeySpec: String,
        val EncryptionAlgorithms: List<String>,
        val KeyUsage: String,
        val PublicKey: Base64Blob,
        val SigningAlgorithms: List<String>
    )
}

object ScheduleKeyDeletion {
    data class Request(val KeyId: KmsKeyId, val PendingWindowInDays: Int? = null)
    data class Response(val KeyId: KmsKeyId, val DeletionDate: Timestamp)
}

object Sign {
    data class Request(
        val KeyId: KmsKeyId,
        val Message: Base64Blob,
        val SigningAlgorithm: String,
        val Request: List<String>? = null,
        val MessageType: String? = null
    )

    data class Response(
        val KeyId: KmsKeyId,
        val Signature: Base64Blob,
        val SigningAlgorithm: String
    )
}

object Verify {
    data class Request(
        val KeyId: String?,
        val Message: Base64Blob,
        val Signature: Base64Blob,
        val SigningAlgorithm: String,
        val MessageType: String? = null,
        val GrantTokens: List<String>? = null
    )

    data class Response(
        val KeyId: KmsKeyId,
        val SignatureValid: Boolean,
        val SigningAlgorithm: String
    )
}
