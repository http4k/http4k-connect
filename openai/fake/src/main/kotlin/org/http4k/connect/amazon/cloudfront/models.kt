package org.http4k.connect.amazon.cloudfront

import org.http4k.connect.openai.ObjectId
import org.http4k.connect.openai.ObjectType
import org.http4k.connect.openai.OpenAIOrg
import org.http4k.connect.openai.OpenAIOrg.Companion.OPENAI
import org.http4k.connect.openai.Timestamp
import org.http4k.connect.openai.action.Model
import org.http4k.connect.openai.action.Permission
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage

val davinciModel = Model(
    ObjectId.of("text-davinci-002"),
    ObjectType.Model,
    Timestamp.of(1649358449),
    OPENAI,
    listOf(
        Permission(
            ObjectId.of("modelperm-otmQSS0hmabtVGHI9QB3bct3"),
            ObjectType.ModelPermission,
            Timestamp.of(1669085501),
            allow_create_engine = false,
            allow_sampling = true,
            allow_logprobs = true,
            allow_search_indices = false,
            allow_view = true,
            allow_fine_tuning = false,
            organization = OpenAIOrg.ALL,
            group = null,
            is_blocking = false
        )
    ),
    ObjectId.of("text-davinci-002"),
    null
)

val curieModel = Model(
    ObjectId.of("text-curie-002"),
    ObjectType.Model,
    Timestamp.of(1649358450),
    OPENAI,
    listOf(
        Permission(
            ObjectId.of("modelperm-49FUp5v084tBB49tC4z8LPH5"),
            ObjectType.ModelPermission,
            Timestamp.of(1669085502),
            allow_create_engine = false,
            allow_sampling = true,
            allow_logprobs = true,
            allow_search_indices = false,
            allow_view = true,
            allow_fine_tuning = false,
            organization = OpenAIOrg.ALL,
            group = null,
            is_blocking = false
        )
    ),
    ObjectId.of("text-curie-002"),
    null
)

val babbageModel = Model(
    ObjectId.of("text-babbage-001"),
    ObjectType.Model,
    Timestamp.of(1649358451),
    OPENAI,
    listOf(
        Permission(
            ObjectId.of("modelperm-W1YUe7GnRk6U8gNvkB9sXsA9"),
            ObjectType.ModelPermission,
            Timestamp.of(1669085503),
            allow_create_engine = false,
            allow_sampling = true,
            allow_logprobs = true,
            allow_search_indices = false,
            allow_view = true,
            allow_fine_tuning = false,
            organization = OpenAIOrg.ALL,
            group = null,
            is_blocking = false
        )
    ),
    ObjectId.of("text-babbage-001"),
    null
)

val DEFAULT_OPEN_AI_MODELS = Storage.InMemory<Model>().apply {
    setOf(babbageModel, curieModel, davinciModel).forEach {
        set(it.id.value, it)
    }
}
