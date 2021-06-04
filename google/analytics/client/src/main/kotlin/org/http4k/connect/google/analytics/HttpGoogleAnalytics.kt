package org.http4k.connect.google.analytics

import org.http4k.client.JavaHttpClient
import org.http4k.connect.google.analytics.action.GoogleAnalyticsAction
import org.http4k.connect.google.analytics.model.TrackingId
import org.http4k.core.HttpHandler
import org.http4k.core.body.form

fun GoogleAnalytics.Companion.Http(http: HttpHandler = JavaHttpClient(), trackingId: TrackingId) =
    object : GoogleAnalytics {
        override fun <R> invoke(action: GoogleAnalyticsAction<R>) = action.toResult(
            http(
                action.toRequest()
                    .form(VERSION, "1")
                    .form(MEASUREMENT_ID, trackingId.value)
            )
        )
    }

const val VERSION = "v"
const val MEASUREMENT_ID = "tid"
