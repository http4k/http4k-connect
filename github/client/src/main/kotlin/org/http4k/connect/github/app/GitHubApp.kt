package org.http4k.connect.github.app

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.github.app.action.GitHubAppAction

@Http4kConnectAdapter
interface GitHubApp {
    operator fun <R> invoke(action: GitHubAppAction<R>): Result<R, RemoteFailure>

    companion object
}
