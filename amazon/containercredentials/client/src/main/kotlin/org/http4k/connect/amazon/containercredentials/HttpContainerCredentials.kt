package org.http4k.connect.amazon.containercredentials

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.containercredentials.action.ContainerCredentialsAction
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom

/**
 * Standard HTTP implementation of ContainerCredentials
 */
fun ContainerCredentials.Companion.Http(http: HttpHandler = JavaHttpClient()) = object : ContainerCredentials {
    private val unauthedHttp = SetBaseUriFrom(Uri.of("http://169.254.170.2"))
        .then(ClientFilters.SetXForwardedHost())
        .then(http)

    override fun <R> invoke(action: ContainerCredentialsAction<R>) =
        action.toResult(unauthedHttp(action.toRequest()))
}
