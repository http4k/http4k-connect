package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.Tag

object CreateKey {
    data class Request(
        val BypassPolicyLockoutSafetyCheck: Boolean? = null,
        val CustomerMasterKeySpec: String? = null,
        val CustomKeyStoreId: String? = null,
        val Description: String? = null,
        val KeyUsage: String? = null,
        val Origin: String? = null,
        val Policy: String? = null,
        val Tags: List<Tag>? = null
    )

    data class Response(val KeyMetadata: KeyMetadata)
}
