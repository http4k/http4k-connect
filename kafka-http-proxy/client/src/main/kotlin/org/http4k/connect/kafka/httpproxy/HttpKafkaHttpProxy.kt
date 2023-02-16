package org.http4k.connect.kafka.httpproxy

import org.http4k.connect.kafka.httpproxy.action.KafkaHttpProxyAction
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.BasicAuth
import org.http4k.filter.ClientFilters.SetHostFrom

/**
 * Standard HTTP implementation of KafkaHttpProxy
 */
fun KafkaHttpProxy.Companion.Http(
    credentials: Credentials,
    baseUri: Uri,
    http: HttpHandler
) = object : KafkaHttpProxy {
    private val http = BasicAuth(credentials)
        .then(SetHostFrom(baseUri))
        .then(http)

    override fun <R : Any> invoke(action: KafkaHttpProxyAction<R>) = action.toResult(this.http(action.toRequest()))
}
