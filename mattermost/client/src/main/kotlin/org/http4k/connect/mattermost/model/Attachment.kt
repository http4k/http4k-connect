@file:OptIn(ExperimentalKotshiApi::class)

package org.http4k.connect.mattermost.model

import se.ansman.kotshi.ExperimentalKotshiApi
import se.ansman.kotshi.JsonProperty
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Attachment(
    val fallback: String,
    val color: String? = null,
    val pretext: String? = null,
    val text: String,
    @JsonProperty("author_name")
    val authorName: String? = null,
    @JsonProperty("author_link")
    val authorLink: String? = null,
    @JsonProperty("author_icon")
    val authorIcon: String? = null,
    val title: String? = null,
    @JsonProperty("title_link")
    val titleLink: String? = null,
    val fields: List<AttachmentField>? = null,
    @JsonProperty("image_url")
    val imageUrl: String? = null,
    @JsonProperty("thumb_url")
    val thumbUrl: String? = null,
    val footer: String? = null,
    @JsonProperty("footer_icon")
    val footerIcon: String? = null,
)

@JsonSerializable
data class AttachmentField(
    val title: String,
    val value: String,
    val short: String? = null,
)
