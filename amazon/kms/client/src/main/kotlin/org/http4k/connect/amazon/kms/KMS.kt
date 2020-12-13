package org.http4k.connect.amazon.kms

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsJsonAction
import org.http4k.connect.amazon.model.AwsService
import kotlin.reflect.KClass

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
abstract class KMSAction<R : Any>(clazz: KClass<R>) : AwsJsonAction<R>(AwsService.of("TrentService"), clazz, KMSMoshi)

/**
 * Docs: https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html
 */
interface KMS {
    operator fun <R : Any> invoke(request: KMSAction<R>): Result<R, RemoteFailure>

    companion object
}
