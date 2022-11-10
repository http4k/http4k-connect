package org.http4k.connect.google.analytics.ua

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.google.analytics.ua.action.GoogleAnalyticsAction

@Http4kConnectAdapter
interface GoogleAnalyticsUA {
    operator fun <R> invoke(action: GoogleAnalyticsAction<R>): Result<R, RemoteFailure>

    companion object
}
