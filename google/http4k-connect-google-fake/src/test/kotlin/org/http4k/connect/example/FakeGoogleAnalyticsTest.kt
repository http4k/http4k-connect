package org.http4k.connect.example

import org.http4k.connect.google.FakeGoogleAnalytics
import org.http4k.connect.google.GoogleAnalytics
import org.junit.jupiter.api.Test

class FakeGoogleAnalyticsTest {
    private val analytics = GoogleAnalytics.Http(FakeGoogleAnalytics(), "SOME_TRACKING_ID")

    @Test
    fun `can log page view`() {
        analytics.pageView("SOME_CLIENT_ID", "title", "/doc/path", "www.http4k.org")
    }
}
