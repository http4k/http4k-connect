package org.http4k.format

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.model.KotshiTagJsonAdapter
import se.ansman.kotshi.KotshiJsonAdapterFactory

open class AwsJsonAdapterFactory(
    vararg typesToAdapters: Pair<String, (Moshi) -> JsonAdapter<*>>
) : SimpleMoshiAdapterFactory(
    *(typesToAdapters.toList() + adapter { KotshiTagJsonAdapter() })
        .toTypedArray()
)

@KotshiJsonAdapterFactory
abstract class AwsCoreJsonAdapterFactory : JsonAdapter.Factory

