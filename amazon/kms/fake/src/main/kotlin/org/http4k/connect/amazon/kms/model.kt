package org.http4k.connect.amazon.kms

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.KMSKeyId
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.kms.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.kms.model.KeyUsage
import org.http4k.connect.amazon.kms.model.SigningAlgorithm
import org.http4k.connect.model.Base64Blob
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

data class StoredCMK(
    val keyId: KMSKeyId,
    val arn: ARN,
    val keyUsage: KeyUsage,
    val customerMasterKeySpec: CustomerMasterKeySpec,
    val privateKeyContent: EncryptionKeyContent?,
    val publicKeyContent: EncryptionKeyContent?,
    val deletion: Timestamp? = null
)

data class EncryptionKeyContent(
    val format: String,
    val encoded: Base64Blob
)

val EncryptionKeyContent.keySpec get() = when(format) {
    "PKCS#8" -> PKCS8EncodedKeySpec(encoded.decodedBytes())
    "X.509" -> X509EncodedKeySpec(encoded.decodedBytes())
    else -> error("Unsupported format: $format")
}

private val StoredCMK.keyFactory get() = when(customerMasterKeySpec) {
    CustomerMasterKeySpec.RSA_2048, CustomerMasterKeySpec.RSA_3072, CustomerMasterKeySpec.RSA_4096 -> KeyFactory.getInstance("RSA")
    CustomerMasterKeySpec.ECC_NIST_P256, CustomerMasterKeySpec.ECC_NIST_P384, CustomerMasterKeySpec.ECC_NIST_P521 -> KeyFactory.getInstance("ECDSA", BouncyCastleProvider())
    CustomerMasterKeySpec.ECC_SECG_P256K1 -> null
    CustomerMasterKeySpec.SYMMETRIC_DEFAULT -> null
}

val StoredCMK.signingAlgorithms get() = when(customerMasterKeySpec) {
    CustomerMasterKeySpec.RSA_2048, CustomerMasterKeySpec.RSA_3072, CustomerMasterKeySpec.RSA_4096 -> listOf(
        SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_256,
        SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_384,
        SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_512,
        SigningAlgorithm.RSASSA_PSS_SHA_256,
        SigningAlgorithm.RSASSA_PSS_SHA_384,
        SigningAlgorithm.RSASSA_PSS_SHA_512
    )
    CustomerMasterKeySpec.ECC_NIST_P256, CustomerMasterKeySpec.ECC_NIST_P384, CustomerMasterKeySpec.ECC_NIST_P521 -> listOf(
        SigningAlgorithm.ECDSA_SHA_256,
        SigningAlgorithm.ECDSA_SHA_384,
        SigningAlgorithm.ECDSA_SHA_512
    )
    CustomerMasterKeySpec.ECC_SECG_P256K1 -> emptyList()
    CustomerMasterKeySpec.SYMMETRIC_DEFAULT -> emptyList()
}

val StoredCMK.publicKey get() = publicKeyContent?.keySpec
    ?.let { keyFactory?.generatePublic(it) }

val StoredCMK.privateKey get() = privateKeyContent?.keySpec
    ?.let { keyFactory?.generatePrivate(it) }
