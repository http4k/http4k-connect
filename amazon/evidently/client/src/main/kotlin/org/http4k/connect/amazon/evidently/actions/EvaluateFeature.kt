package org.http4k.connect.amazon.evidently.actions

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.evidently.EvidentlyAction
import org.http4k.connect.amazon.evidently.model.EntityId
import org.http4k.connect.amazon.evidently.model.EvaluationContext
import org.http4k.connect.amazon.evidently.model.FeatureName
import org.http4k.connect.amazon.evidently.model.ProjectName
import org.http4k.connect.amazon.evidently.model.VariationName
import org.http4k.core.Uri
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
data class EvaluateFeature(
    val project: ProjectName,
    val feature: FeatureName,
    val entityId: EntityId,
    val evaluationContext: EvaluationContext?
): EvidentlyAction<EvaluatedFeature>(EvaluatedFeature::class, dataPlane = true) {
    override fun uri() = Uri.of("/projects/$project/evaluations/$feature")
    override fun requestBody() = EvaluateFeatureRequest(entityId, evaluationContext)
}

@JsonSerializable
data class VariableValue(
    val boolValue: Boolean?,
    val doubleValue: Double?,
    val longValue: Long?,
    val stringValue: String?
) {
    constructor(boolValue: Boolean): this(boolValue = boolValue, doubleValue = null, longValue = null, stringValue = null)
    constructor(doubleValue: Double): this(boolValue = null, doubleValue = doubleValue, longValue = null, stringValue = null)
    constructor(longValue: Long): this(boolValue = null, doubleValue = null, longValue = longValue, stringValue = null)
    constructor(stringValue: String): this(boolValue = null, doubleValue = null, longValue = null, stringValue = stringValue)
}

@JsonSerializable
data class EvaluatedFeature(
    val details: String,
    val reason: String,
    val value: VariableValue,
    val variation: VariationName
)

@JsonSerializable
data class EvaluateFeatureRequest(
    val entityId: EntityId,
    val evaluationContext: EvaluationContext?
)
