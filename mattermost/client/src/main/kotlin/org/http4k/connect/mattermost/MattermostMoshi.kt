package org.http4k.connect.mattermost

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

object MattermostMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(MattermostJsonAdapterFactory)
        .asConfigurable()
        .withStandardMappings()
        .done()
)

@KotshiJsonAdapterFactory
object MattermostJsonAdapterFactory : JsonAdapter.Factory by KotshiMattermostJsonAdapterFactory