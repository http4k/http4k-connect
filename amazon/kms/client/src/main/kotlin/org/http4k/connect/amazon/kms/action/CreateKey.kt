package org.http4k.connect.amazon.kms.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.KeyUsage
import org.http4k.connect.amazon.model.Tag

@Http4kConnectAction
data class CreateKey(
    val CustomerMasterKeySpec: CustomerMasterKeySpec? = null,
    val KeyUsage: KeyUsage? = null,
    val BypassPolicyLockoutSafetyCheck: Boolean? = null,
    val CustomKeyStoreId: String? = null,
    val Description: String? = null,
    val Origin: String? = null,
    val Policy: String? = null,
    val Tags: List<Tag>? = null
) : KMSAction<KeyCreated>(KeyCreated::class)

data class KeyCreated(val KeyMetadata: KeyMetadata)
