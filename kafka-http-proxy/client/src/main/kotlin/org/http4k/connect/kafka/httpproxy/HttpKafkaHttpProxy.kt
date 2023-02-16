package org.http4k.connect.kafka.httpproxy

import org.http4k.connect.kafka.httpproxy.action.KafkaHttpProxyAction
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.ClientFilters.BasicAuth

/**
 * Standard HTTP implementation of KafkaHttpProxy
 */
fun KafkaHttpProxy.Companion.Http(credentials: Credentials, http: HttpHandler) = object : KafkaHttpProxy {
    private val http = BasicAuth(credentials).then(http)

    override fun <R : Any> invoke(action: KafkaHttpProxyAction<R>) = action.toResult(this.http(action.toRequest()))
}
