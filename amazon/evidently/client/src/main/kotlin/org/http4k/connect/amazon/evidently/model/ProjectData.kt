package org.http4k.connect.amazon.evidently.model

import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.Timestamp
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ProjectData(
    val activeExperimentCount: Int?,
    val activeLaunchCount: Int?,
    // appConfigResource
    val arn: ARN,
    val createdTime: Timestamp,
    // dataDelivery
    val description: String?,
    val experimentCount: Int?,
    val featureCount: Int?,
    val lastUpdatedTime: Timestamp,
    val launchCount: Int?,
    val name: ProjectName,
    val status: String,
    val tags: Map<String, String>?
)

@JsonSerializable
data class CreateProjectResponse(
    val project: ProjectData
)
