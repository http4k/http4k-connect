package org.http4k.connect.ollama

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://github.com/ollama/ollama/blob/main/docs/api.md
 */
@Http4kConnectAdapter
interface Ollama {
    operator fun <R> invoke(action: OllamaAction<R>): Result<R, RemoteFailure>

    companion object
}
