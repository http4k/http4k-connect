package org.http4k.connect.github

import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Secret
import org.http4k.connect.github.action.GitHubAction
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom

fun GitHub.Companion.Http(token: () -> Secret, http: HttpHandler = JavaHttpClient(), authScheme: String = "token") =
    object : GitHub {
        private val routedHttp = SetBaseUriFrom(Uri.of("https://api.github.com"))
            .then(http)

        override fun <R : Any> invoke(action: GitHubAction<R>) = action.toResult(
            routedHttp(
                action.toRequest()
                    .header("Authorization", "$authScheme ${token().use { it }}")
                    .header("Accept", "application/vnd.github.v3+json")
            )
        )
    }
