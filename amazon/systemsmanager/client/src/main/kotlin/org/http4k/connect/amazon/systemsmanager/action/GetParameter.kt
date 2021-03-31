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
    val ARN: ARN? = null,
    val Name: SSMParameterName? = null,
    val Value: String? = null,
    val Type: ParameterType? = null,
    val DataType: String? = null,
    val Version: Long? = null,
    val LastModifiedDate: Timestamp? = null,
    val Selector: String? = null,
    val SourceResult: String? = null)

@JsonSerializable
data class ParameterValue(
    val Parameter: Parameter
)
