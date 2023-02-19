package org.http4k.connect.kafka.rest.action.consumer

import org.http4k.connect.kafka.rest.action.AbstractKafkaRestAction
import kotlin.reflect.KClass

abstract class KafkaRestConsumerAction<R : Any>(clazz: KClass<R>) : AbstractKafkaRestAction<R>(clazz)
