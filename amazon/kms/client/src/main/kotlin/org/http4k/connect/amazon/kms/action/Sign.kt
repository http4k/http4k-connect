package org.http4k.connect.amazon.kms.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.SigningAlgorithm
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class Sign(
    val KeyId: KMSKeyId,
    val Message: Base64Blob,
    val SigningAlgorithm: SigningAlgorithm,
    val Request: List<String>? = null,
    val MessageType: String? = null
) : KMSAction<Signed>(Signed::class)

@JsonSerializable
data class Signed(
    val KeyId: KMSKeyId,
    val Signature: Base64Blob,
    val SigningAlgorithm: SigningAlgorithm
)
