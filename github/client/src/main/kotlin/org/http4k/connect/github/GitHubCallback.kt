package org.http4k.connect.github

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.github.action.GitHubCallbackAction

@Http4kConnectAdapter
interface GitHubCallback {
    operator fun <R> invoke(action: GitHubCallbackAction<R>): Result<R, RemoteFailure>

    companion object
}

