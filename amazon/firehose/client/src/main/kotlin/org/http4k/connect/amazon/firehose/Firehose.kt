package org.http4k.connect.amazon.firehose

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion
import org.http4k.connect.amazon.firehose.action.FirehoseAction

/**
 * Docs: https://docs.aws.amazon.com/firehose/latest/APIReference/Welcome.html
 */
@Http4kConnectAdapter
interface Firehose {
    operator fun <R : Any> invoke(action: FirehoseAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("firehose")
}
