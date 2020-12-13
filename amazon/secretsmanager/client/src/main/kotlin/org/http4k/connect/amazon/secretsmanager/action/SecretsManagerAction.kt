package org.http4k.connect.amazon.secretsmanager.action

import org.http4k.connect.amazon.AwsJsonAction
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.secretsmanager.SecretsManagerMoshi
import kotlin.reflect.KClass

abstract class SecretsManagerAction<R : Any>(clazz: KClass<R>) : AwsJsonAction<R>(AwsService.of("secretsmanager"), clazz, SecretsManagerMoshi)
