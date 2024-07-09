package org.http4k.connect.lmstudio

import dev.forkhandles.values.LongValue
import dev.forkhandles.values.LongValueFactory
import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.minValue
import org.http4k.connect.model.ModelName
import java.time.Instant
import java.time.Instant.ofEpochSecond


class Org private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<Org>(::Org) {
        val ALL = Org.of("*")
        val DEFAULT = Org.of("organization-owner")
    }
}

class ObjectType private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ObjectType>(::ObjectType) {
        val List = ObjectType.of("list")
        val Model = ObjectType.of("model")
        val ChatCompletion = ObjectType.of("chat.completion")
        val ChatCompletionChunk = ObjectType.of("chat.completion.chunk")
        val Embedding = ObjectType.of("embedding")
        val ModelPermission = ObjectType.of("model_permission")
    }
}

class ObjectId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ObjectId>(::ObjectId)
}

class Timestamp private constructor(value: Long) : LongValue(value) {
    fun toInstant(): Instant = ofEpochSecond(value)

    companion object : LongValueFactory<Timestamp>(::Timestamp, 0L.minValue) {
        fun of(value: Instant) = of(value.epochSecond)
    }
}

val ModelName.Companion.CHAT_MODEL get() = ModelName.of("chat-model")
val ModelName.Companion.EMBEDDING_MODEL get() = ModelName.of("embedding-model")

class ResponseFormatType private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ResponseFormatType>(::ResponseFormatType) {
        val JsonObject = ResponseFormatType.of("json_object")
        val url = ResponseFormatType.of("url")
    }
}

class Role private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<Role>(::Role) {
        val System = Role.of("system")
        val User = Role.of("user")
        val Assistant = Role.of("assistant")
    }
}

class TokenId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<TokenId>(::TokenId)
}

class User private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<User>(::User)
}

class CompletionId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<CompletionId>(::CompletionId)
}

