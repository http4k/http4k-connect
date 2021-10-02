package org.http4k.connect.github.callback

import org.http4k.connect.github.GitHubToken
import org.http4k.connect.github.api.action.GitHubCallbackAction
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.SignGitHubCallbackSha256

fun GitHubCallback.Companion.Http(url: Uri, token: () -> GitHubToken, http: HttpHandler) = object : GitHubCallback {
    private val signedHttp = SetBaseUriFrom(url)
        .then(ClientFilters.SignGitHubCallbackSha256(token))
        .then(http)

    override fun invoke(action: GitHubCallbackAction) = action.toResult(signedHttp(action.toRequest()))
}
