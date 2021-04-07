package org.http4k.format

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.core.model.KotshiTagJsonAdapter

class AwsCoreJsonAdapterFactory(
    vararg typesToAdapters: Pair<String, (Moshi) -> JsonAdapter<*>>
) : SimpleMoshiAdapterFactory(
    *(typesToAdapters.toList() + adapter { KotshiTagJsonAdapter() })
        .toTypedArray()
)

