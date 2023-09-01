package org.http4k.connect.amazon.evidently.model

import org.http4k.connect.amazon.evidently.actions.VariableValue
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class VariationConfig(
    val name: VariationName,
    val value: VariableValue
)
