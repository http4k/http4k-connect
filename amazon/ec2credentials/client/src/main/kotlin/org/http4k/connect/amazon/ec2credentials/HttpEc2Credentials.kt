package org.http4k.connect.amazon.ec2credentials

import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.ClientFilters.SetBaseUriFrom

/**
 * Standard HTTP implementation of Ec2Credentials
 */
fun Ec2InstanceMetadata.Companion.Http(http: HttpHandler = JavaHttpClient()) = object : Ec2InstanceMetadata {
    private val unauthedHttp = SetBaseUriFrom(Uri.of("http://169.254.169.254"))
        .then(ClientFilters.SetXForwardedHost())
        .then(http)

    override fun <R> invoke(action: Ec2CredentialsAction<R>) =
        action.toResult(unauthedHttp(action.toRequest()))
}
