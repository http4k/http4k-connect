package org.http4k.connect

import kotlin.reflect.KClass

inline fun <reified T : Any> kClass(): KClass<T> = T::class
