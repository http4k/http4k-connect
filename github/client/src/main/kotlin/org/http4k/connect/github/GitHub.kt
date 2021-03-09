package org.http4k.connect.github

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.github.action.GitHubAction

@Http4kConnectAdapter
interface GitHub {
    operator fun <R : Any> invoke(action: GitHubAction<R>): Result<R, RemoteFailure>

    companion object
}

