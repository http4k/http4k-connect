package org.http4k.connect.google.analytics.ua

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.google.analytics.CollectPageViewContractTest
import org.http4k.connect.google.analytics.model.AnalyticsCollector
import org.http4k.connect.google.analytics.model.DEFAULT_USER_AGENT
import org.http4k.connect.google.analytics.ua.action.CLIENT_ID
import org.http4k.connect.google.analytics.ua.action.DOCUMENT_HOST
import org.http4k.connect.google.analytics.ua.action.DOCUMENT_PATH
import org.http4k.connect.google.analytics.ua.action.DOCUMENT_TITLE
import org.http4k.connect.google.analytics.ua.model.TrackingId
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.body.form

class CollectPageViewTest : CollectPageViewContractTest() {
    override val collector: AnalyticsCollector
        get() = GoogleAnalytics.Http(TrackingId.of(trackingId), testHttpClient)::collect

    override fun assertPageView(title: String, path: String, host: String) {
        assertThat(
            testHttpClient.captured, equalTo(
                Request(POST, "https://www.google-analytics.com/collect")
                    .header("User-Agent", DEFAULT_USER_AGENT)
                    .header("Host", "www.google-analytics.com")
                    .form(CLIENT_ID, clientId.value)
                    .form(DOCUMENT_TITLE, title)
                    .form(DOCUMENT_PATH, path)
                    .form(DOCUMENT_HOST, host)
                    .form(VERSION, "1")
                    .form(TRACKING_ID, trackingId)
            )
        )
    }

    override fun assertNoPageView() {
        assertThat(testHttpClient.captured, absent())
    }

}
