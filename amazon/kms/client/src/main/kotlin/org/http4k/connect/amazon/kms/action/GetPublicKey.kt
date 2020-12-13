package org.http4k.connect.amazon.kms.action

import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.model.EncryptionAlgorithm
import org.http4k.connect.amazon.model.KeyUsage
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.SigningAlgorithm

data class GetPublicKey(
    val KeyId: KmsKeyId,
    val GrantTokens: List<String>? = null
) : KMSAction<PublicKey>(PublicKey::class)

data class PublicKey(
    val KeyId: KmsKeyId,
    val CustomerMasterKeySpec: CustomerMasterKeySpec,
    val EncryptionAlgorithms: List<EncryptionAlgorithm>,
    val KeyUsage: KeyUsage,
    val PublicKey: Base64Blob,
    val SigningAlgorithms: List<SigningAlgorithm>?
)
