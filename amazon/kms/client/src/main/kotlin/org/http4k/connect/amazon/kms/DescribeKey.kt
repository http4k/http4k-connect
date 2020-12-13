package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.KmsKeyId

data class DescribeKey(val KeyId: KmsKeyId, val GrantTokens: List<String>? = null)
    : KMSAction<KeyDescription>(KeyDescription::class)

data class KeyDescription(val KeyMetadata: KeyMetadata)

