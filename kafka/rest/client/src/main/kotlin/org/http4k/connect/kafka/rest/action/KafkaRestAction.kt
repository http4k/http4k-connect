package org.http4k.connect.kafka.rest.action

import kotlin.reflect.KClass

abstract class KafkaRestAction<R : Any>(clazz: KClass<R>) : AbstractKafkaRestAction<R>(clazz)
