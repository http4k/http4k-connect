package org.http4k.connect.amazon.evidently.actions

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.evidently.EvidentlyAction
import org.http4k.connect.amazon.evidently.model.CreateFeatureData
import org.http4k.connect.amazon.evidently.model.CreateFeatureResponse
import org.http4k.connect.amazon.evidently.model.EntityId
import org.http4k.connect.amazon.evidently.model.EvaluationStrategy
import org.http4k.connect.amazon.evidently.model.FeatureName
import org.http4k.connect.amazon.evidently.model.ProjectName
import org.http4k.connect.amazon.evidently.model.VariationConfig
import org.http4k.connect.amazon.evidently.model.VariationName
import org.http4k.core.Uri

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
