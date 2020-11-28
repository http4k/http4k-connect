package org.http4k.connect.amazon.kms

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html
 */
interface KMS {
    fun createKey(request: CreateKey.Request): Result<CreateKey.Response, RemoteFailure>
    fun describeKey(request: DescribeKey.Request): Result<DescribeKey.Response, RemoteFailure>
    fun decrypt(request: Decrypt.Request): Result<Decrypt.Response, RemoteFailure>
    fun encrypt(request: Encrypt.Request): Result<Encrypt.Response, RemoteFailure>
    fun getPublicKey(request: GetPublicKey.Request): Result<GetPublicKey.Response, RemoteFailure>
    fun scheduleKeyDelete(request: ScheduleKeyDeletion.Request): Result<ScheduleKeyDeletion.Response, RemoteFailure>
    fun sign(request: Sign.Request): Result<Sign.Response, RemoteFailure>
    fun verify(request: Verify.Request): Result<Verify.Response, RemoteFailure>

    companion object
}

