package org.http4k.connect.amazon.ses

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion
import org.http4k.connect.amazon.ses.action.SESAction

/**
 * Docs: https://docs.aws.amazon.com/ses/latest/APIReference/Welcome.html
 */
@Http4kConnectAdapter
interface SES {
    operator fun <R> invoke(action: SESAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("email")
}
