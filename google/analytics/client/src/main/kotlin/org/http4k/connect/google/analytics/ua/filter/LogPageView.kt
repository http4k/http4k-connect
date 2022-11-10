package org.http4k.connect.google.analytics.ua.filter

import org.http4k.connect.google.analytics.ua.GoogleAnalyticsUA
import org.http4k.connect.google.analytics.ua.model.ClientId
import org.http4k.connect.google.analytics.ua.pageView
import org.http4k.core.Filter
import org.http4k.core.Request
import org.http4k.routing.RoutedRequest
import java.util.UUID

/**
 * Log page view to Google Analytics
 */
fun LogPageView(
    analytics: GoogleAnalyticsUA,
    clientId: (Request) -> ClientId = { ClientId.of(UUID.randomUUID().toString()) }
): Filter = Filter { handler ->
    { request ->
        handler(request).also {
            if (it.status.successful || it.status.informational || it.status.redirection) {
                val host = request.header("host") ?: request.uri.host
                val path = when (request) {
                    is RoutedRequest -> request.xUriTemplate.toString()
                    else -> request.uri.path
                }
                val userAgent = it.header("User-Agent") ?: DEFAULT_USER_AGENT
                analytics.pageView(userAgent, clientId(request), path, path, host)
            }
        }
    }
}

