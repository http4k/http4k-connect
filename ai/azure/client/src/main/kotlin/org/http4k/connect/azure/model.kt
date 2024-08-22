package org.http4k.connect.azure

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import org.http4k.connect.model.ModelName
import org.http4k.connect.model.Timestamp
import se.ansman.kotshi.JsonProperty
import se.ansman.kotshi.JsonSerializable

class Region private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<Region>(::Region)
}

class AzureHost private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<AzureHost>(::AzureHost)
}

class AzureAIApiKey private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<AzureAIApiKey>(::AzureAIApiKey)
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

enum class Quality {
    standard, hd
}

enum class Style {
    vivid, natural
}

val ModelName.Companion.GPT4 get() = ModelName.of("gpt-4")
val ModelName.Companion.DALL_E_2 get() = ModelName.of("dall-e-2")
val ModelName.Companion.GPT4_TURBO_PREVIEW get() = ModelName.of("gpt-4-turbo-preview")
val ModelName.Companion.GPT3_5 get() = ModelName.of("gpt-3.5-turbo")
val ModelName.Companion.TEXT_EMBEDDING_ADA_002 get() = ModelName.of("text-embedding-ada-002")

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

