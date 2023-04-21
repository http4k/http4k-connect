package org.http4k.connect.mattermost

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.mattermost.action.MattermostAction

@Http4kConnectAdapter
interface Mattermost {
    operator fun <R> invoke(action: MattermostAction<R>): Result<R, RemoteFailure>

    companion object
}
