package org.http4k.connect.amazon.evidently.actions

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.evidently.EvidentlyAction
import org.http4k.connect.amazon.evidently.model.CreateProjectData
import org.http4k.connect.amazon.evidently.model.CreateProjectResponse
import org.http4k.connect.amazon.evidently.model.ProjectName
import org.http4k.core.Uri

@Http4kConnectAction
data class CreateProject(
    val name: ProjectName,
    val description: String?,
    val tags: Map<String, String>?
) : EvidentlyAction<CreateProjectResponse>(CreateProjectResponse::class) {
    override fun uri() = Uri.of("/projects")

    override fun requestBody() = CreateProjectData(
        name = name,
        description = description,
        tags = tags
    )
}
