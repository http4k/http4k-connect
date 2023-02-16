package org.http4k.connect.kafka.httpproxy

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.connect.kafka.httpproxy.model.ConsumerName
import org.http4k.connect.kafka.httpproxy.model.Offset
import org.http4k.connect.kafka.httpproxy.model.PartitionId
import org.http4k.connect.kafka.httpproxy.model.SchemaId
import org.http4k.connect.kafka.httpproxy.model.Topic
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

object KafkaHttpProxyMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(KafkaHttpProxyJsonAdapterFactory)
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .withStandardMappings()
        .value(ConsumerGroup)
        .value(ConsumerName)
        .value(ConsumerInstanceId)
        .value(Offset)
        .value(PartitionId)
        .value(SchemaId)
        .value(Topic)
        .done()
)

@KotshiJsonAdapterFactory
object KafkaHttpProxyJsonAdapterFactory : JsonAdapter.Factory by KotshiKafkaHttpProxyJsonAdapterFactory
