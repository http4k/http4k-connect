package org.http4k.connect.amazon.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class KeyMetadata(
    val KeyId: KMSKeyId,
    val Arn: ARN? = null,
    val AWSAccountId: AwsAccount? = null,
    val KeyUsage: KeyUsage? = null,
    val EncryptionAlgorithms: List<EncryptionAlgorithm>? = null,
    val SigningAlgorithms: List<SigningAlgorithm>? = null,
    val CustomerMasterKeySpec: CustomerMasterKeySpec? = null,
    val Enabled: Boolean? = null,
    val CreationDate: Timestamp? = null,
    val CloudHsmClusterId: String? = null,
    val CustomKeyStoreId: String? = null,
    val DeletionDate: Timestamp? = null,
    val Description: String? = null,
    val ExpirationModel: String? = null,
    val KeyManager: String? = null,
    val KeyState: String? = null,
    val Origin: String? = null,
    val ValidTo: Timestamp? = null
)

enum class SigningAlgorithm {
    RSASSA_PSS_SHA_256,
    RSASSA_PSS_SHA_384,
    RSASSA_PSS_SHA_512,
    RSASSA_PKCS1_V1_5_SHA_256,
    RSASSA_PKCS1_V1_5_SHA_384,
    RSASSA_PKCS1_V1_5_SHA_512,
    ECDSA_SHA_256,
    ECDSA_SHA_384,
    ECDSA_SHA_512
}

enum class EncryptionAlgorithm {
    SYMMETRIC_DEFAULT,
    RSAES_OAEP_SHA_1,
    RSAES_OAEP_SHA_256
}

enum class CustomerMasterKeySpec {
    RSA_2048,
    RSA_3072,
    RSA_4096,
    ECC_NIST_P256,
    ECC_NIST_P384,
    ECC_NIST_P521,
    ECC_SECG_P256K1,
    SYMMETRIC_DEFAULT
}

enum class KeyUsage {
    SIGN_VERIFY,
    ENCRYPT_DECRYPT
}
