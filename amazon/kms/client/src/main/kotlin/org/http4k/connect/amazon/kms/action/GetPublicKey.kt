package org.http4k.connect.amazon.kms.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.model.EncryptionAlgorithm
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.KeyUsage
import org.http4k.connect.amazon.model.SigningAlgorithm
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class GetPublicKey(
    val KeyId: KMSKeyId,
    val GrantTokens: List<String>? = null
) : KMSAction<PublicKey>(PublicKey::class)

@JsonSerializable
data class PublicKey(
    val KeyId: KMSKeyId,
    val CustomerMasterKeySpec: CustomerMasterKeySpec,
    val EncryptionAlgorithms: List<EncryptionAlgorithm>,
    val KeyUsage: KeyUsage,
    val PublicKey: Base64Blob,
    val SigningAlgorithms: List<SigningAlgorithm>?
)
