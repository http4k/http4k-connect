package org.http4k.connect.amazon.kms

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html
 */
interface KMS {
    fun create(request: CreateKeyRequest): Result<CreateKeyResponse, RemoteFailure>
    fun describe(request: DescribeKeyRequest): Result<DescribeKeyResponse, RemoteFailure>
    fun decrypt(request: DecryptRequest): Result<DecryptResponse, RemoteFailure>
    fun encrypt(request: EncryptRequest): Result<EncryptResponse, RemoteFailure>
    fun getPublicKey(request: GetPublicKeyRequest): Result<GetPublicKeyResponse, RemoteFailure>
    fun scheduleDeletion(request: ScheduleKeyDeletionRequest): Result<ScheduleKeyDeletionResponse, RemoteFailure>
    fun sign(request: SignRequest): Result<SignResponse, RemoteFailure>
    fun verify(request: VerifyRequest): Result<VerifyResponse, RemoteFailure>

    companion object
}

