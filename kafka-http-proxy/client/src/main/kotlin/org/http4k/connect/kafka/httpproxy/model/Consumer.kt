package org.http4k.connect.kafka.httpproxy.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable
import java.time.Duration

@JsonSerializable
data class Consumer(
    val name: ConsumerName,
    val format: RecordFormat,
    @Json(name = "auto.offset.reset") val reset: String,
    @Json(name = "auto.commit.enable") val enableAutocommit: String? = null,
    @Json(name = "fetch.min.bytes") val minBytes: String? = null,
    @Json(name = "consumer.request.timeout.ms") val timeout: Duration? = null
)
