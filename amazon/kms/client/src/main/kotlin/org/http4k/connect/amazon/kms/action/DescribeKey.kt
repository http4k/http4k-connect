package org.http4k.connect.amazon.kms.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.KeyMetadata

@Http4kConnectAction
data class DescribeKey(val KeyId: KMSKeyId, val GrantTokens: List<String>? = null)
    : KMSAction<KeyDescription>(KeyDescription::class)

data class KeyDescription(val KeyMetadata: KeyMetadata)

