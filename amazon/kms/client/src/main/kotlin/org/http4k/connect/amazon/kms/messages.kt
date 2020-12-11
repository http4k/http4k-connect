package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.model.EncryptionAlgorithm
import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.KeyUsage
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.SigningAlgorithm
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.Timestamp

data class CreateKeyRequest(
    val CustomerMasterKeySpec: CustomerMasterKeySpec? = null,
    val KeyUsage: KeyUsage? = null,
    val BypassPolicyLockoutSafetyCheck: Boolean? = null,
    val CustomKeyStoreId: String? = null,
    val Description: String? = null,
    val Origin: String? = null,
    val Policy: String? = null,
    val Tags: List<Tag>? = null
)

data class CreateKeyResponse(val KeyMetadata: KeyMetadata)

data class DescribeKeyRequest(val KeyId: KmsKeyId, val GrantTokens: List<String>? = null)

data class DescribeKeyResponse(val KeyMetadata: KeyMetadata)

data class DecryptRequest(
    val KeyId: KmsKeyId,
    val CiphertextBlob: Base64Blob,
    val EncryptionAlgorithm: EncryptionAlgorithm? = null,
    val EncryptionContext: Map<String, String>? = null,
    val GrantTokens: List<String>? = null
)

data class DecryptResponse(val KeyId: KmsKeyId, val Plaintext: Base64Blob, val EncryptionAlgorithm: EncryptionAlgorithm)

data class EncryptRequest(
    val KeyId: KmsKeyId,
    val Plaintext: Base64Blob,
    val EncryptionAlgorithm: EncryptionAlgorithm? = null,
    val EncryptionContext: Map<String, String>? = null,
    val GrantTokens: List<String>? = null
)

data class EncryptResponse(val KeyId: KmsKeyId, val CiphertextBlob: Base64Blob, val EncryptionAlgorithm: EncryptionAlgorithm)

data class GetPublicKeyRequest(
    val KeyId: KmsKeyId,
    val GrantTokens: List<String>? = null
)

data class GetPublicKeyResponse(
    val KeyId: KmsKeyId,
    val CustomerMasterKeySpec: CustomerMasterKeySpec,
    val EncryptionAlgorithms: List<EncryptionAlgorithm>,
    val KeyUsage: KeyUsage,
    val PublicKey: Base64Blob,
    val SigningAlgorithms: List<SigningAlgorithm>?
)

data class ScheduleKeyDeletionRequest(val KeyId: KmsKeyId, val PendingWindowInDays: Int? = null)
data class ScheduleKeyDeletionResponse(val KeyId: KmsKeyId, val DeletionDate: Timestamp)

data class SignRequest(
    val KeyId: KmsKeyId,
    val Message: Base64Blob,
    val SigningAlgorithm: SigningAlgorithm,
    val Request: List<String>? = null,
    val MessageType: String? = null
)

data class SignResponse(
    val KeyId: KmsKeyId,
    val Signature: Base64Blob,
    val SigningAlgorithm: SigningAlgorithm
)

data class VerifyRequest(
    val KeyId: KmsKeyId,
    val Message: Base64Blob,
    val Signature: Base64Blob,
    val SigningAlgorithm: SigningAlgorithm,
    val MessageType: String? = null,
    val GrantTokens: List<String>? = null
)

data class VerifyResponse(
    val KeyId: KmsKeyId,
    val SignatureValid: Boolean,
    val SigningAlgorithm: SigningAlgorithm
)
