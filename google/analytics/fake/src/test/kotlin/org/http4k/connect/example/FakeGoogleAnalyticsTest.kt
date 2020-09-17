package org.http4k.connect.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.common.success
import org.http4k.connect.google.FakeGoogleAnalytics
import org.http4k.connect.google.analytics.ClientId
import org.http4k.connect.google.analytics.GoogleAnalytics
import org.http4k.connect.google.analytics.Http
import org.http4k.connect.google.analytics.TrackingId
import org.junit.jupiter.api.Test

class FakeGoogleAnalyticsTest : FakeSystemContract() {
    private val http = FakeGoogleAnalytics()
    private val analytics = GoogleAnalytics.Http(http, TrackingId("SOME_TRACKING_ID"))

    @Test
    fun `can log page view`() {
        assertThat(
            analytics.pageView("some-user-agent", ClientId("SOME_CLIENT_ID"), "title", "/doc/path", "www.http4k.org"),
            equalTo(success(Unit)))
    }
}

