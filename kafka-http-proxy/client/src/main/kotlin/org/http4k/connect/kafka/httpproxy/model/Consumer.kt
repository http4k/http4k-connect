package org.http4k.connect.kafka.httpproxy.model

import com.squareup.moshi.Json
import org.http4k.connect.kafka.httpproxy.model.AutoCommitEnable.`true`
import org.http4k.connect.kafka.httpproxy.model.AutoOffsetReset.latest
import se.ansman.kotshi.JsonSerializable
import java.time.Duration

@JsonSerializable
data class Consumer(
    val name: ConsumerName,
    val format: RecordFormat,
    @Json(name = "auto.offset.reset") val reset: AutoOffsetReset = latest,
    @Json(name = "auto.commit.enable") val enableAutocommit: AutoCommitEnable = `true`,
    @Json(name = "fetch.min.bytes") val minBytes: String? = null,
    @Json(name = "consumer.request.timeout.ms") val timeout: Duration? = null
)

