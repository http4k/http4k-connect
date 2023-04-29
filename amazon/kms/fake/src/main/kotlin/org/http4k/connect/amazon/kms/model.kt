package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.KMSKeyId
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.kms.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.kms.model.KeyUsage
import org.http4k.connect.model.Base64Blob

data class StoredCMK(
    val keyId: KMSKeyId,
    val arn: ARN,
    val keyUsage: KeyUsage,
    val customerMasterKeySpec: CustomerMasterKeySpec,
    val deletion: Timestamp? = null
)

data class KeyPair(val public: Base64Blob, val private: Base64Blob, val password: String)
