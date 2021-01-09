package org.http4k.connect

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

abstract class ConnectJsonAdapterFactory(vararg typesToAdapters: Pair<String, (Moshi) -> JsonAdapter<*>>) : JsonAdapter.Factory {
    private val mappings = typesToAdapters.toMap()

    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi) =
        mappings[Types.getRawType(type).typeName]?.let { it(moshi) }
}

inline fun <reified T : JsonAdapter<K>, reified K> adapter(noinline fn: (Moshi) -> T) = K::class.java.name to fn
