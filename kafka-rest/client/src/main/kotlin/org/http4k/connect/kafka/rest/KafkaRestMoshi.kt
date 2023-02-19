package org.http4k.connect.kafka.rest

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.kafka.rest.model.ConsumerGroup
import org.http4k.connect.kafka.rest.model.ConsumerInstanceId
import org.http4k.connect.kafka.rest.model.ConsumerName
import org.http4k.connect.kafka.rest.model.ConsumerRequestTimeout
import org.http4k.connect.kafka.rest.model.Offset
import org.http4k.connect.kafka.rest.model.PartitionId
import org.http4k.connect.kafka.rest.model.SchemaId
import org.http4k.connect.kafka.rest.model.Topic
import org.http4k.connect.model.Base64Blob
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings
import org.http4k.lens.BiDiMapping
import se.ansman.kotshi.KotshiJsonAdapterFactory
import java.time.Duration

object KafkaRestMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(KafkaRestJsonAdapterFactory)
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .withStandardMappings()
        .value(Base64Blob)
        .value(ConsumerGroup)
        .value(ConsumerName)
        .text(BiDiMapping(ConsumerRequestTimeout::class.java,
            { ConsumerRequestTimeout.of(Duration.ofMillis(it.toLong())) }, { it.value.toMillis().toString() })
        )
        .value(ConsumerInstanceId)
        .value(Offset)
        .value(PartitionId)
        .value(SchemaId)
        .value(Topic)
        .done()
)

@KotshiJsonAdapterFactory
object KafkaRestJsonAdapterFactory : JsonAdapter.Factory by KotshiKafkaRestJsonAdapterFactory
