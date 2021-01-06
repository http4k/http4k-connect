package org.http4k.connect.amazon.sqs

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion
import org.http4k.connect.amazon.sqs.action.SQSAction

/**
 * Docs: https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html
 */
@Http4kConnectAdapter
interface SQS {
    operator fun <R> invoke(action: SQSAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("sqs")
}
