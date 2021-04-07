package org.http4k.connect.amazon.kms.model

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.KMSKeyId
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class KeyEntry(val KeyId: KMSKeyId, val KeyArn: ARN)
