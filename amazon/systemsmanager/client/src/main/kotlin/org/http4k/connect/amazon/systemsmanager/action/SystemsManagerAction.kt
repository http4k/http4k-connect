package org.http4k.connect.amazon.systemsmanager.action

import org.http4k.connect.amazon.AwsJsonAction
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.systemsmanager.SystemsManagerMoshi
import kotlin.reflect.KClass

/**
 * Available actions:
 *  DeleteParameter
 *  GetParameter
 *  PutParameter
 */
abstract class SystemsManagerAction<R : Any>(clazz: KClass<R>) : AwsJsonAction<R>(AwsService.of("AmazonSSM"), clazz, SystemsManagerMoshi)
