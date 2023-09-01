package org.http4k.connect.amazon.evidently.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class CreateFeatureData(
    val defaultVariation: VariationName,
    val description: String?,
    val entityOverrides: Map<String, VariationName>?,
    val evaluationStrategy: EvaluationStrategy?,
    val name: FeatureName,
    val tags: Map<String, String>?,
    val variations: List<VariationConfig>
)

enum class EvaluationStrategy { ALL_RULES, DEFAULT_VARIATION }
