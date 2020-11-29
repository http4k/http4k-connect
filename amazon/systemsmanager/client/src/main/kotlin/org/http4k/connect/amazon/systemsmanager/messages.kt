package org.http4k.connect.amazon.systemsmanager

import com.fasterxml.jackson.annotation.JsonProperty
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.Timestamp

object DeleteParameter {
    data class Request(
        val Name: String
    )
}

object GetParameter {
    data class Request(
        val Name: String,
        val WithDecryption: Boolean? = null,
    )

    data class Parameter(
        @JsonProperty("ARN") val arn: ARN?,
        val Name: String?,
        val Value: String?,
        val Type: ParameterType?,
        val DataType: String?,
        val Version: Long?,
        val LastModifiedDate: Timestamp?,
        val Selector: String?,
        val SourceResult: String?
    )

    data class Response(
        val Parameter: Parameter
    )
}

object PutParameter {
    data class Request(
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
    )

    data class Response(
        val Tier: String,
        val Version: Int
    )
}
