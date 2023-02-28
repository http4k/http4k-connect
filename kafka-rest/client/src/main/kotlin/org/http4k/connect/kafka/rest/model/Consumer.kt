package org.http4k.connect.kafka.rest.model

import com.squareup.moshi.Json
import org.http4k.connect.kafka.rest.model.AutoCommitEnable.`true`
import org.http4k.connect.kafka.rest.model.AutoOffsetReset.latest
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Consumer(
    val name: ConsumerInstance,
    val format: RecordFormat,
    @Json(name = "auto.offset.reset") val reset: AutoOffsetReset = latest,
    @Json(name = "auto.commit.enable") val enableAutocommit: AutoCommitEnable = `true`,
    @Json(name = "fetch.min.bytes") val minBytes: String? = null,
    @Json(name = "consumer.request.timeout.ms") val timeout: ConsumerRequestTimeout? = null
)

