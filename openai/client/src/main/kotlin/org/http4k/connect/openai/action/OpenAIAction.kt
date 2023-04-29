package org.http4k.connect.openai.action

import dev.forkhandles.result4k.Result
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure

interface OpenAIAction<R> : Action<Result<R, RemoteFailure>>
