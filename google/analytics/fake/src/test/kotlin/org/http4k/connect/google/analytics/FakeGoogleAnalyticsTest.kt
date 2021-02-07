package org.http4k.connect.google.analytics

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.connect.google.FakeGoogleAnalytics
import org.http4k.connect.google.analytics.action.PageView
import org.http4k.connect.google.model.ClientId
import org.http4k.connect.google.model.TrackingId
import org.junit.jupiter.api.Test

class FakeGoogleAnalyticsTest {
    private val analytics = GoogleAnalytics.Http(FakeGoogleAnalytics())

    @Test
    fun `can log page view`() {
        assertThat(
            analytics(
                PageView(
                    "some-user-agent",
                    ClientId.of("SOME_CLIENT_ID"),
                    "title",
                    "/doc/path",
                    "www.http4k.org",
                    TrackingId.of("SOME_TRACKING_ID")
                )
            ),
            equalTo(Success(Unit))
        )
    }
}

