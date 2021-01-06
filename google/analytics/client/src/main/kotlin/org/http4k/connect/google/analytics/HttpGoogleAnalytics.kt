package org.http4k.connect.google.analytics

import org.http4k.client.JavaHttpClient
import org.http4k.connect.google.analytics.action.GoogleAnalyticsAction
import org.http4k.connect.google.model.TrackingId
import org.http4k.core.HttpHandler

fun GoogleAnalytics.Companion.Http(trackingId: TrackingId,
                                   http: HttpHandler = JavaHttpClient()) = object : GoogleAnalytics {
    override fun <R> invoke(action: GoogleAnalyticsAction<R>) = action.toResult(http(action.toRequest(trackingId)))
}
