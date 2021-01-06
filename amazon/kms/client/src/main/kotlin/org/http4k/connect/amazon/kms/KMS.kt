package org.http4k.connect.amazon.kms

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion
import org.http4k.connect.amazon.kms.action.KMSAction

/**
 * Docs: https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html
 */
@Http4kConnectAdapter
interface KMS {
    operator fun <R : Any> invoke(request: KMSAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("kms")
}
