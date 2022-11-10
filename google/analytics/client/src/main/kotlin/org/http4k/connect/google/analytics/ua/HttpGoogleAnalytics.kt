package org.http4k.connect.google.analytics.ua

import org.http4k.client.JavaHttpClient
import org.http4k.connect.google.analytics.ua.action.GoogleAnalyticsAction
import org.http4k.connect.google.analytics.ua.model.TrackingId
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.debug

fun GoogleAnalytics.Companion.Http(trackingId: TrackingId, rawHttp: HttpHandler = JavaHttpClient().debug()) =
    object : GoogleAnalytics {
        private val http = SetBaseUriFrom(Uri.of("https://www.google-analytics.com")).then(rawHttp)

        override fun <R> invoke(action: GoogleAnalyticsAction<R>) = action.toResult(
            http(
                action.toRequest()
                    .form(VERSION, "1")
                    .form(TRACKING_ID, trackingId.value)
            )
        )
    }

const val VERSION = "v"
const val TRACKING_ID = "tid"
