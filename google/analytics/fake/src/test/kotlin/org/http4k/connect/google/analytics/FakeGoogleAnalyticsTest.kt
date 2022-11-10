package org.http4k.connect.google.analytics

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.connect.google.FakeGoogleAnalytics
import org.http4k.connect.google.analytics.model.ClientId
import org.http4k.connect.google.analytics.model.Event
import org.http4k.connect.google.analytics.model.PageView
import org.http4k.connect.google.analytics.ua.GoogleAnalytics
import org.http4k.connect.google.analytics.ua.Http
import org.http4k.connect.google.analytics.ua.collect
import org.http4k.connect.google.analytics.ua.model.TrackingId
import org.junit.jupiter.api.Test

class FakeGoogleAnalyticsTest {
    private val analytics = GoogleAnalytics.Http(TrackingId.of("SOME_TRACKING_ID"), FakeGoogleAnalytics())

    @Test
    fun `can log page view`() {
        assertThat(
            analytics.collect(PageView("title", "/doc/path", "www.http4k.org", ClientId.of("SOME_CLIENT_ID"), "some-user-agent")),
            equalTo(Success(Unit))
        )
    }

    @Test
    fun `can log event`() {
        assertThat(
            analytics.collect(Event("event", "action", "label", 1, ClientId.of("SOME_CLIENT_ID"), "some-user-agent")),
            equalTo(Success(Unit))
        )
    }
}

