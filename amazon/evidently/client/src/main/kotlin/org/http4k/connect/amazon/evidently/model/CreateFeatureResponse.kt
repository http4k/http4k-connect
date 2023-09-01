package org.http4k.connect.amazon.evidently.model

import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.Timestamp
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class CreateFeatureResponse(
    val feature: FeatureData
)

@JsonSerializable
data class FeatureData(
    val arn: ARN,
    val createdTime: Timestamp,
    val evaluationStrategy: EvaluationStrategy,
    val lastUpdatedTime: Timestamp,
    val name: FeatureName,
    val status: FeatureStatus,
    val valueType: ValueType,
    val variations: List<VariationConfig>,
    val defaultVariation: VariationName,
    val description: String?,
    val entityOverrides: Map<String, VariationName>?,
    val evaluationRules: List<EvaluationRule>?,
    val project: ARN,
    val tags: Map<String, String>?
)

enum class FeatureStatus { AVAILABLE, UPDATING }

enum class ValueType { STRING, LONG, DOUBLE, BOOLEAN }

@JsonSerializable
data class EvaluationRule(
    val type: String,
    val name: String
)
