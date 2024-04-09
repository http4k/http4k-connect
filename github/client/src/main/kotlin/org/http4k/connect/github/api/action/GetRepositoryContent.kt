package org.http4k.connect.github.api.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.github.api.schemas.ContentTree
import org.http4k.connect.github.model.Owner
import org.http4k.connect.github.model.Path
import org.http4k.connect.github.model.Repo
import org.http4k.connect.kClass
import org.http4k.core.Method.GET
import org.http4k.core.Request

@Http4kConnectAction
data class GetRepositoryContent(val owner: Owner,
                                val repo: Repo,
                                val path: Path) : NonNullGitHubAction<ContentTree>(kClass()) {
    override fun toRequest() = Request(GET, "/repos/$owner/$repo/contents/$path")
}
