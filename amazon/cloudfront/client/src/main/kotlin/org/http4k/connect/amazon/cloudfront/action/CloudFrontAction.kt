package org.http4k.connect.amazon.cloudfront.action

import dev.forkhandles.result4k.Result
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure

interface CloudFrontAction<R> : Action<Result<R, RemoteFailure>>
