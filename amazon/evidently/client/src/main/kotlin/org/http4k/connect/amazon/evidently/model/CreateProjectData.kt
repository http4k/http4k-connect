package org.http4k.connect.amazon.evidently.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class CreateProjectData(
    val name: ProjectName,
    val description: String?,
    val tags: Map<String, String>?,
    // appConfigResource,
    // dataDelivery
)
