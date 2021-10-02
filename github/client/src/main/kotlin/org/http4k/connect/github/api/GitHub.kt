package org.http4k.connect.github.api

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.github.api.action.GitHubAction

@Http4kConnectAdapter
interface GitHub {
    operator fun <R> invoke(action: GitHubAction<R>): Result<R, RemoteFailure>

    companion object
}

