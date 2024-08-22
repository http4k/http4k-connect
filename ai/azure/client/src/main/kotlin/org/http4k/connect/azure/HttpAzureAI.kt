package org.http4k.connect.azure

import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.BearerAuth
import org.http4k.filter.ClientFilters.SetBaseUriFrom

fun AzureAI.Companion.Http(
    token: AzureAIApiKey,
    host: AzureHost,
    region: Region,
    http: HttpHandler = JavaHttpClient()
) = object : AzureAI {

    private val routedHttp = SetBaseUriFrom(Uri.of("https://${host}.${region}.inference.ai.azure.com"))
        .then(BearerAuth(token.value))
        .then(http)

    override fun <R> invoke(action: AzureAIAction<R>) = action.toResult(routedHttp(action.toRequest()))
}
