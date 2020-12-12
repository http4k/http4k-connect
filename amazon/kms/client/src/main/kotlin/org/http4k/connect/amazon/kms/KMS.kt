package org.http4k.connect.amazon.kms

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html
 */
interface KMS {
    operator fun invoke(request: CreateKey): Result<KeyCreated, RemoteFailure>
    operator fun invoke(request: DescribeKey): Result<KeyDescription, RemoteFailure>
    operator fun invoke(request: Decrypt): Result<Decrypted, RemoteFailure>
    operator fun invoke(request: Encrypt): Result<Encrypted, RemoteFailure>
    operator fun invoke(request: GetPublicKey): Result<PublicKey, RemoteFailure>
    operator fun invoke(request: ScheduleKeyDeletion): Result<KeyDeletionSchedule, RemoteFailure>
    operator fun invoke(request: Sign): Result<Signed, RemoteFailure>
    operator fun invoke(request: Verify): Result<VerifyResult, RemoteFailure>

    companion object
}

