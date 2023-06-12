package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion

/**
 * Docs: https://docs.aws.amazon.com/secretsmanager/latest/apireference/Welcome.html
 */
@Http4kConnectAdapter
interface SecretsManager {
    operator fun <R : Any> invoke(action: SecretsManagerAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("secretsmanager")
}
