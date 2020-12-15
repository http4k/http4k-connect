package org.http4k.connect.amazon.kms

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.kms.action.KMSAction

/**
 * Docs: https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html
 */

@Http4kConnectAdapter
interface KMS {
    /**
     * Available actions:
     *  CreateKey
     *  DescribeKey
     *  Decrypt
     *  Encrypt
     *  GetPublicKey
     *  ScheduleKeyDeletion
     *  Sign
     *  Verify
     */
    operator fun <R : Any> invoke(request: KMSAction<R>): Result<R, RemoteFailure>

    companion object
}
