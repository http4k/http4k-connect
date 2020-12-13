package org.http4k.connect.google.analytics

import org.http4k.client.JavaHttpClient
import org.http4k.connect.google.analytics.action.GoogleAnalyticsAction
import org.http4k.connect.google.model.TrackingId
import org.http4k.core.HttpHandler

fun GoogleAnalytics.Companion.Http(trackingId: TrackingId,
                                   http: HttpHandler = JavaHttpClient()) = object : GoogleAnalytics {
    override fun <R> invoke(request: GoogleAnalyticsAction<R>) = request.toResult(http(request.toRequest(trackingId)))
}
