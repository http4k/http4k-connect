package org.http4k.connect.amazon.systemsmanager.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.model.Tag

@Http4kConnectAction
data class PutParameter(
    val Name: String,
    val Value: String,
    val Type: ParameterType,
    val KeyId: KMSKeyId? = null,
    val Overwrite: Boolean? = null,
    val AllowedPattern: String? = null,
    val DataType: String? = null,
    val Description: String? = null,
    val Policies: List<String>? = null,
    val Tags: List<Tag>? = null,
    val Tier: String? = null
) : SystemsManagerAction<PutParameterResult>(PutParameterResult::class)

data class PutParameterResult(
    val Tier: String,
    val Version: Int
)
