package org.http4k.format

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.ConnectJsonAdapterFactory
import org.http4k.connect.adapter
import org.http4k.connect.amazon.model.KotshiTagJsonAdapter

abstract class AwsJsonAdapterFactory(
    vararg typesToAdapters: Pair<String, (Moshi) -> JsonAdapter<*>>
) : ConnectJsonAdapterFactory(
    *(typesToAdapters.toList() + adapter { KotshiTagJsonAdapter() })
        .toTypedArray()
)
