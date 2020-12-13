package org.http4k.connect.google.analytics.action

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.google.model.TrackingId
import org.http4k.core.Request
import org.http4k.core.Response

interface GoogleAnalyticsAction<R> {
    fun toRequest(trackingId: TrackingId): Request
    fun toResult(response: Response): Result<R, RemoteFailure>
}
