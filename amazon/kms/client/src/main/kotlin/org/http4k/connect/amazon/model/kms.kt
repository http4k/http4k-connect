package org.http4k.connect.amazon.model

import com.fasterxml.jackson.annotation.JsonProperty

data class KeyMetadata(
    val KeyId: KmsKeyId,
    @JsonProperty("ARN") val ARN: String? = null,
    val AWSAccountId: AwsAccount? = null,
    val CloudHsmClusterId: String? = null,
    val CreationDate: Number? = null,
    val CustomerMasterKeySpec: String? = null,
    val CustomKeyStoreId: String? = null,
    val DeletionDate: Timestamp?,
    val Description: String? = null,
    val Enabled: Boolean?,
    val EncryptionAlgorithms: List<String>?,
    val ExpirationModel: String? = null,
    val KeyManager: String? = null,
    val KeyState: String? = null,
    val KeyUsage: String? = null,
    val Origin: String? = null,
    val SigningAlgorithms: List<String>? = null,
    val ValidTo: Timestamp? = null
)
