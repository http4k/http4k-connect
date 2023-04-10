package org.http4k.connect.openai

import dev.forkhandles.values.LongValue
import dev.forkhandles.values.LongValueFactory
import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.minValue
import java.time.Instant
import java.time.Instant.ofEpochSecond

class OpenAIToken private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<OpenAIToken>(::OpenAIToken)
}

class OpenAIOrg private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<OpenAIOrg>(::OpenAIOrg) {
        val ALL = OpenAIOrg.of("*")
        val OPENAI = OpenAIOrg.of("openai")
    }
}

class ObjectType private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ObjectType>(::ObjectType) {
        val List = ObjectType.of("list")
        val Model = ObjectType.of("model")
        val ChatCompletion = ObjectType.of("chat.completion")
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

class ModelName private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ModelName>(::ModelName) {
        val GPT4 = ModelName.of("gpt-4")
        val GPT3_5 = ModelName.of("gpt-3.5-turbo")
    }
}

class Role private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<Role>(::Role) {
        val System = Role.of("system")
        val User = Role.of("user")
    }
}

class TokenId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<TokenId>(::TokenId)
}

class User private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<User>(::User)
}

class Content private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<Content>(::Content)
}

class CompletionId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<CompletionId>(::CompletionId)
}
