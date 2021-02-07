package org.http4k.filter

import org.http4k.connect.google.analytics.GoogleAnalytics
import org.http4k.connect.google.analytics.action.PageView
import org.http4k.connect.google.model.ClientId
import org.http4k.connect.google.model.TrackingId
import org.http4k.core.Filter
import org.http4k.core.Request
import org.http4k.routing.RoutedRequest
import java.util.UUID

/**
 * Log page view to Google Analytics
 */
fun ServerFilters.LogPageView(
    analytics: GoogleAnalytics,
    trackingId: TrackingId,
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
                analytics(PageView(userAgent, clientId(request), path, path, host, trackingId))
            }
        }
    }
}

const val DEFAULT_USER_AGENT =
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/600.7.12 (KHTML, like Gecko) Version/8.0.7 Safari/600.7.12"
