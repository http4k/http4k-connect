package org.http4k.connect.amazon.evidently.actions

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.evidently.EvidentlyAction
import org.http4k.connect.amazon.evidently.model.EntityId
import org.http4k.connect.amazon.evidently.model.EvaluationStrategy
import org.http4k.connect.amazon.evidently.model.FeatureName
import org.http4k.connect.amazon.evidently.model.ProjectName
import org.http4k.connect.amazon.evidently.model.VariationName
import org.http4k.core.Uri
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
data class CreateFeature(
    val project: ProjectName,
    val defaultVariation: VariationName,
    val description: String?,
    val entityOverrides: Map<EntityId, VariationName>?,
    val evaluationStrategy: EvaluationStrategy?,
    val name: FeatureName,
    val tags: Map<String, String>?,
    val variations: Map<VariationName, VariableValue>
) : EvidentlyAction<CreateFeatureResponse>(CreateFeatureResponse::class) {

    override fun uri() = Uri.of("/projects/$project/features")

    override fun requestBody() = CreateFeatureData(
        defaultVariation = defaultVariation,
        description = description,
        entityOverrides = entityOverrides?.mapKeys { it.key.value },
        evaluationStrategy = evaluationStrategy,
        name = name,
        tags = tags,
        variations = variations.map { VariationConfig(it.key, it.value) }
    )
}

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

@JsonSerializable
data class CreateFeatureResponse(
    val feature: Feature
)

@JsonSerializable
data class Feature(
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

@JsonSerializable
data class VariationConfig(
    val name: VariationName,
    val value: VariableValue
)
