package org.http4k.connect.kafka.httpproxy.action.consumer

import org.http4k.connect.kafka.httpproxy.action.AbstractKafkaHttpProxyAction
import kotlin.reflect.KClass

abstract class KafkaHttpProxyConsumerAction<R : Any>(clazz: KClass<R>) : AbstractKafkaHttpProxyAction<R>(clazz)
