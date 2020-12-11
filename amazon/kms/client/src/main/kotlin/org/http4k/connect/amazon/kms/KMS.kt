package org.http4k.connect.amazon.kms

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html
 */
interface KMS {
    operator fun invoke(request: CreateKeyRequest): Result<CreateKeyResponse, RemoteFailure>
    operator fun invoke(request: DescribeKeyRequest): Result<DescribeKeyResponse, RemoteFailure>
    operator fun invoke(request: DecryptRequest): Result<DecryptResponse, RemoteFailure>
    operator fun invoke(request: EncryptRequest): Result<EncryptResponse, RemoteFailure>
    operator fun invoke(request: GetPublicKeyRequest): Result<GetPublicKeyResponse, RemoteFailure>
    operator fun invoke(request: ScheduleKeyDeletionRequest): Result<ScheduleKeyDeletionResponse, RemoteFailure>
    operator fun invoke(request: SignRequest): Result<SignResponse, RemoteFailure>
    operator fun invoke(request: VerifyRequest): Result<VerifyResponse, RemoteFailure>

    companion object
}

