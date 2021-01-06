package org.http4k.connect.amazon.sts

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion
import org.http4k.connect.amazon.sts.action.STSAction

/**
 * Docs: https://docs.aws.amazon.com/STS/latest/APIReference/welcome.html
 */
@Http4kConnectAdapter
interface STS {
    operator fun <R> invoke(action: STSAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("sts")
}
