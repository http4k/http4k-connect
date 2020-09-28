package org.http4k.connect.google.analytics

import org.http4k.connect.FakeSystemContract
import org.http4k.connect.google.FakeGoogleAnalytics
import org.http4k.core.Method
import org.http4k.core.Request

class FakeGoogleAnalyticsChaosTest : FakeSystemContract(FakeGoogleAnalytics()) {
    override val anyValidRequest = Request(Method.POST, "/collect")
}
