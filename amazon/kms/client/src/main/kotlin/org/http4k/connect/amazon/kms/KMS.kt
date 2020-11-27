package org.http4k.connect.amazon.kms

/**
 * Docs: https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html
 */
interface KMS {
    fun createKey(request: CreateKey.Request): CreateKey.Response
    fun describe(request: DescribeKey.Request): DescribeKey.Response
    fun decrypt(request: Decrypt.Request): Decrypt.Response
    fun encrypt(request: Encrypt.Request): Encrypt.Response
    fun getPublicKey(request: GetPublicKey.Request): GetPublicKey.Response
    fun scheduleKeyDelete(request: ScheduleKeyDeletion.Request): ScheduleKeyDeletion.Response
    fun sign(request: Sign.Request): Sign.Response
    fun verify(request: Verify.Request): Verify.Response

    companion object
}

