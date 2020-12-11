package org.http4k.connect.amazon.sqs

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html
 */
interface SQS {
    operator fun invoke(request: SendMessageRequest): Result<Unit, RemoteFailure>

    companion object
}

