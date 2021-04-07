package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.kms.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.kms.model.KeyUsage
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.Timestamp

data class StoredCMK(
    val keyId: KMSKeyId,
    val arn: ARN,
    val keyUsage: KeyUsage,
    val customerMasterKeySpec: CustomerMasterKeySpec,
    val deletion: Timestamp? = null
)
