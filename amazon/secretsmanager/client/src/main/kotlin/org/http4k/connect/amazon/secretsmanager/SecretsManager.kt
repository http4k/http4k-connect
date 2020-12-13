package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsJsonAction
import org.http4k.connect.amazon.model.AwsService
import kotlin.reflect.KClass

/**
 * Docs: https://docs.aws.amazon.com/secretsmanager/latest/apireference/Welcome.html
 */
abstract class SecretsManagerAction<R : Any>(clazz: KClass<R>) : AwsJsonAction<R>(AwsService.of("secretsmanager"), clazz, SecretsManagerMoshi)

interface SecretsManager {
    operator fun <R : Any> invoke(request: SecretsManagerAction<R>): Result<R, RemoteFailure>

    companion object
}
