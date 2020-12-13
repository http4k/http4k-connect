package org.http4k.connect.amazon.systemsmanager.action

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.model.Timestamp

data class GetParameter(
    val Name: String,
    val WithDecryption: Boolean? = null,
): SystemsManagerAction<ParameterValue>(ParameterValue::class)

data class Parameter(
    val ARN: ARN?,
    val Name: String?,
    val Value: String?,
    val Type: ParameterType?,
    val DataType: String?,
    val Version: Long?,
    val LastModifiedDate: Timestamp?,
    val Selector: String?,
    val SourceResult: String?
)

data class ParameterValue(
    val Parameter: Parameter
)
