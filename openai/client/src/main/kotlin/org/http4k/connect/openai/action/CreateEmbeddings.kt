@file:OptIn(ExperimentalKotshiApi::class)

package org.http4k.connect.openai.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.NonNullAutoMarshalledAction
import org.http4k.connect.kClass
import org.http4k.connect.openai.Content
import org.http4k.connect.openai.ModelName
import org.http4k.connect.openai.ObjectType
import org.http4k.connect.openai.ObjectType.Companion.Embedding
import org.http4k.connect.openai.ObjectType.Companion.List
import org.http4k.connect.openai.OpenAIAction
import org.http4k.connect.openai.OpenAIMoshi
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import se.ansman.kotshi.ExperimentalKotshiApi
import se.ansman.kotshi.JsonProperty
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class CreateEmbeddings(val model: ModelName, val input: List<Content>) :
    NonNullAutoMarshalledAction<Embeddings>(kClass(), OpenAIMoshi),
    OpenAIAction<Embeddings> {
    override fun toRequest() = Request(POST, "/v1/embeddings")
        .with(OpenAIMoshi.autoBody<CreateEmbeddings>().toLens() of this)
}

@JsonSerializable
data class Embedding(val embedding: List<Float>, val index: Int) {
    @JsonProperty(name = "object")
    val objectType: ObjectType = Embedding
}

@JsonSerializable
data class Embeddings(val `data`: List<Embedding>, val model: ModelName, val usage: Usage) {
    @JsonProperty(name = "object")
    val objectType: ObjectType = List
}
