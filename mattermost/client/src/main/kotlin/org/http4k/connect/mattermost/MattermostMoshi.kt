package org.http4k.connect.mattermost

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.mattermost.model.*
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

object MattermostMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(MattermostJsonAdapterFactory)
        .asConfigurable()
        .withStandardMappings()
        .value(EmojiName)
        .value(HexColour)
        .value(PostActionId)
        .value(PostActionName)
        .value(PostActionStyle)
        .value(PostActionOptionText)
        .value(PostActionOptionValue)
        .value(PostActionCookie)
        .done()
)

@KotshiJsonAdapterFactory
object MattermostJsonAdapterFactory : JsonAdapter.Factory by KotshiMattermostJsonAdapterFactory
