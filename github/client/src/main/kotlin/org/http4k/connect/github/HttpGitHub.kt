package org.http4k.connect.github

import org.http4k.cloudnative.env.Secret
import org.http4k.connect.github.action.GitHubAction
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters

fun GitHub.Companion.Http(token: () -> Secret, rawHttp: HttpHandler) = object : GitHub {
    private val http = ClientFilters.SetBaseUriFrom(Uri.of("https://api.github.com"))
        .then(rawHttp)

    override fun <R : Any> invoke(action: GitHubAction<R>) = action.toResult(
        http(
            action.toRequest()
                .header("Authorization", "token ${token()}")
                .header("Accept", "application/vnd.github.v3+json")
        )
    )
}
