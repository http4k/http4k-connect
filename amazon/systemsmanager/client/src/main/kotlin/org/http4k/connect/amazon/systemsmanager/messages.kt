package org.http4k.connect.amazon.systemsmanager

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Timestamp

data class Tag(
    val Key: String?,
    val Value: String?
)

object DeleteParameter {
    data class Request(
        val Name: String
    )

    object Response
}

object GetParameter {
    data class Request(
        val Name: String,
        val WithDecryption: Boolean? = null,
    )

    data class Parameter(
        val ARN: ARN?,
        val Name: String?,
        val DataType: String?,
        val LastModifiedDate: Timestamp?,
        val Selector: String?,
        val SourceResult: String?,
        val Type: String?,
        val Value: String?,
        val Version: Long?
    )

    data class Response(
        val Parameter: Parameter
    )
}

object PutParameter {
    data class Request(
        val Name: String,
        val Value: String,
        val Overwrite: Boolean? = null,
        val AllowedPattern: String? = null,
        val DataType: String? = null,
        val Description: String?,
        val KeyId: String? = null,
        val Policies: List<String>? = null,
        val Tags: List<Tag>? = null,
        val Tier: String? = null,
        val Type: String? = null
    )

    data class Response(
        val Tier: String,
        val Version: Long
    )
}
