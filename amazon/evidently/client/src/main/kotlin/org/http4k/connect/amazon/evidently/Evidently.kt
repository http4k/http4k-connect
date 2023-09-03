package org.http4k.connect.amazon.evidently

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion

@Http4kConnectAdapter
interface Evidently {
    operator fun <R : Any> invoke(action: EvidentlyAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("evidently")
}
