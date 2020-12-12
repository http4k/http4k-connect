package org.http4k.connect.amazon.sqs

import dev.forkhandles.result4k.Result
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html
 */
interface SQSAction<R> : Action<R>

interface SQS {
    operator fun <R> invoke(request: SQSAction<R>): Result<R, RemoteFailure>

    companion object
}

