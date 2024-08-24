package org.http4k.connect.anthropic.action

import org.http4k.connect.anthropic.MediaType
import org.http4k.connect.anthropic.SourceType
import org.http4k.connect.anthropic.ToolName
import org.http4k.connect.anthropic.Type
import org.http4k.connect.anthropic.UserId
import org.http4k.connect.model.Role
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Source(
    val type: SourceType,
    val media_type: MediaType,
    val data: String
)

@JsonSerializable
data class Content(val type: Type, val text: String? = null, val source: Source? = null, val index: Int? = null)

@JsonSerializable
data class Message(val role: Role, val content: List<Content>)

@JsonSerializable
data class Tool(val name: ToolName, val description: String, val inputSchema: Schema)

@JsonSerializable
data class Schema(val type: String, val properties: Map<String, Any>, val required: List<String>)

@JsonSerializable
data class Metadata(val user_id: UserId?)

@JsonSerializable
data class Usage(
    val input_tokens: Int? = null,
    val cache_creation_input_tokens: Int? = null,
    val cache_read_input_tokens: Int? = null,
    val output_tokens: Int? = null
)
