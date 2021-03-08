package org.http4k.connect.github

import org.http4k.cloudnative.env.Secret
import org.http4k.connect.github.action.GitHubCallbackAction
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.SignGitHubCallbackSha256

fun GitHubCallback.Companion.Http(url: Uri, secret: () -> Secret, rawHttp: HttpHandler) = object : GitHubCallback {
    val http = ClientFilters.SetBaseUriFrom(url)
        .then(ClientFilters.SignGitHubCallbackSha256(secret))
        .then(rawHttp)

    override fun invoke(action: GitHubCallbackAction) = action.toResult(http(action.toRequest()))
}
