package org.http4k.connect.github

import org.http4k.connect.github.action.GitHubCallbackAction
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters

fun GitHubCallback.Http(url: Uri, rawHttp: HttpHandler) = object : GitHubCallback {
    val http = ClientFilters.SetBaseUriFrom(url)
        .then(rawHttp)

    override fun <R> invoke(action: GitHubCallbackAction<R>) = action.toResult(http(action.toRequest()))
}
