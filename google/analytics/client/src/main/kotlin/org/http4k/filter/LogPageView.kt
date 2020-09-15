package org.http4k.filter

import org.http4k.connect.google.GoogleAnalytics
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.routing.RoutedRequest
import java.util.UUID

/**
 * Log page view to Google Analytics
 */
fun ServerFilters.LogPageView(analytics: GoogleAnalytics, clientId: (Request) -> String = { UUID.randomUUID().toString() }): Filter = object : Filter {
    override fun invoke(handler: HttpHandler): HttpHandler = { request ->
        handler(request).also {
            if (it.status.successful || it.status.informational || it.status.redirection) {
                val host = request.header("host") ?: request.uri.host
                val path = when (request) {
                    is RoutedRequest -> request.xUriTemplate.toString()
                    else -> request.uri.path
                }
                analytics.pageView(clientId(request), path, path, host)
            }
        }
    }
}
