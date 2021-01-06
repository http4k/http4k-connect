package org.http4k.connect.amazon.systemsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion
import org.http4k.connect.amazon.systemsmanager.action.SystemsManagerAction

/**
 * Docs: https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html
 */
@Http4kConnectAdapter
interface SystemsManager {
    operator fun <R : Any> invoke(request: SystemsManagerAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("ssm")
}
