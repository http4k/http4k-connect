package org.http4k.connect.lmstudio.action

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Usage(
    val prompt_tokens: Int?,
    val completion_tokens: Int?,
    val total_tokens: Int?
)
