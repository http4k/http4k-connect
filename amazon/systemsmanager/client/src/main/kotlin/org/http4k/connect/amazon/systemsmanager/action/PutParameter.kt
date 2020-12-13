package org.http4k.connect.amazon.systemsmanager

import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.systemsmanager.action.SystemsManagerAction

data class PutParameter(
    val Name: String,
    val Value: String,
    val Type: ParameterType,
    val KeyId: KmsKeyId? = null,
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
