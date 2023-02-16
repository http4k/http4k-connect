package org.http4k.connect.kafka.httpproxy.action

import kotlin.reflect.KClass

abstract class KafkaHttpProxyAction<R : Any>(clazz: KClass<R>) : AbstractKafkaHttpProxyAction<R>(clazz)
