package org.http4k.connect.amazon.kms.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.KMSKeyId
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class ListKeys(
    val Limit: Int? = null,
    val Marker: String? = null
) : KMSAction<KeyList>(KeyList::class)

@JsonSerializable
data class KeyEntry(val KeyId: KMSKeyId, val KeyArn: ARN)

@JsonSerializable
data class KeyList(val Keys: List<KeyEntry>)
