package org.http4k.connect.openai

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://platform.openai.com/docs/api-reference
 */
@Http4kConnectAdapter
interface OpenAI {
    operator fun <R> invoke(action: OpenAIAction<R>): Result<R, RemoteFailure>

    companion object
}
