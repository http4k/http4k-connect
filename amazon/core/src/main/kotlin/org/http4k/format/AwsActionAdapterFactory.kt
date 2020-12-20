package org.http4k.format

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.ActionAdapterFactory
import org.http4k.connect.adapter
import org.http4k.connect.amazon.model.KotshiTagJsonAdapter

abstract class AwsActionAdapterFactory(
    vararg typesToAdapters: Pair<String, (Moshi) -> JsonAdapter<*>>
) : ActionAdapterFactory(
    *(typesToAdapters.toList() + adapter { KotshiTagJsonAdapter() })
        .toTypedArray()
)
