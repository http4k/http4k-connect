package org.http4k.connect.amazon.s3.action

import dev.forkhandles.result4k.Result
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure

interface S3BucketAction<R> : Action<Result<R, RemoteFailure>>
