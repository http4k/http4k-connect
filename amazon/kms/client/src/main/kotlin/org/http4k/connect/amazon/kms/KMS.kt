package org.http4k.connect.amazon.kms

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AmazonJsonAction
import org.http4k.connect.amazon.model.AwsService
import kotlin.reflect.KClass

/**
 * Docs: https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html
 */
abstract class KMSAction<R : Any>(clazz: KClass<R>) : AmazonJsonAction<R>(AwsService.of("TrentService"), clazz, KMSMoshi)

interface KMS {
    operator fun <R : Any> invoke(request: KMSAction<R>): Result<R, RemoteFailure>

    companion object
}
