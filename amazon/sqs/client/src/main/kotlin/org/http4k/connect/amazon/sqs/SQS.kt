package org.http4k.connect.amazon.sqs

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion

/**
 * Docs: https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html
 */
@Http4kConnectAdapter
interface SQS {
    operator fun <RO: Any, R: Any> invoke(action: SQSAction<RO, R>): Result<RO, RemoteFailure>

    companion object : AwsServiceCompanion("sqs")
}
