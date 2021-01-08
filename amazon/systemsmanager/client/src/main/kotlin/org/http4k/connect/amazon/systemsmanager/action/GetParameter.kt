package org.http4k.connect.amazon.systemsmanager.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.model.SSMParameterName
import org.http4k.connect.amazon.model.Timestamp
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class GetParameter(
    val Name: SSMParameterName,
    val WithDecryption: Boolean? = null,
) : SystemsManagerAction<ParameterValue>(ParameterValue::class)

@JsonSerializable
data class Parameter(
    val ARN: ARN?,
    val Name: SSMParameterName?,
    val Value: String?,
    val Type: ParameterType?,
    val DataType: String?,
    val Version: Long?,
    val LastModifiedDate: Timestamp?,
    val Selector: String?,
    val SourceResult: String?
)

@JsonSerializable
data class ParameterValue(
    val Parameter: Parameter
)
