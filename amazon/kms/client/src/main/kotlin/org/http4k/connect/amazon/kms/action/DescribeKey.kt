package org.http4k.connect.amazon.kms.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.KeyMetadata
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class DescribeKey(val KeyId: KMSKeyId, val GrantTokens: List<String>? = null)
    : KMSAction<KeyDescription>(KeyDescription::class)

@JsonSerializable
data class KeyDescription(val KeyMetadata: KeyMetadata)

