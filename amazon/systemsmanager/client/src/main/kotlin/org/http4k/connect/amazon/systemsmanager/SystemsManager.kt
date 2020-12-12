package org.http4k.connect.amazon.systemsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AmazonJsonAction
import org.http4k.connect.amazon.model.AwsService
import kotlin.reflect.KClass

/**
 * Docs: https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html
 */
abstract class SystemsManagerAction<R : Any>(clazz: KClass<R>) : AmazonJsonAction<R>(AwsService.of("AmazonSSM"), clazz, SystemsManagerMoshi)

interface SystemsManager {
    operator fun <R : Any> invoke(request: SystemsManagerAction<R>): Result<R, RemoteFailure>

    companion object
}
