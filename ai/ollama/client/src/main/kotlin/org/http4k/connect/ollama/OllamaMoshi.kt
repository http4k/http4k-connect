package org.http4k.connect.ollama

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.model.ModelName
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

object OllamaMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(OllamaJsonAdapterFactory)
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .value(ModelName)
        .value(Prompt)
        .value(SystemMessage)
        .value(Template)
        .withStandardMappings()
        .done()
)

@KotshiJsonAdapterFactory
object OllamaJsonAdapterFactory : JsonAdapter.Factory by KotshiOllamaJsonAdapterFactory
