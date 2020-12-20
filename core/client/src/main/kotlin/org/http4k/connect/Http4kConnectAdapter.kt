package org.http4k.connect

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

@Target(AnnotationTarget.CLASS)
annotation class Http4kConnectAdapter

@Target(AnnotationTarget.CLASS)
annotation class Http4kConnectAction

abstract class ActionAdapterFactory(vararg typesToAdapters: Pair<Class<*>, (Moshi) -> JsonAdapter<*>>) : JsonAdapter.Factory {
    private val mappings: Map<Class<*>, (Moshi) -> JsonAdapter<*>> = typesToAdapters.toMap()

    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi) =
        if (annotations.isNotEmpty()) null
        else
            mappings[Types.getRawType(type)]?.let { it(moshi) }
}

inline fun <reified T : JsonAdapter<*>> adapter(noinline fn: (Moshi) -> T) =
    T::class.java to fn
