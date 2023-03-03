package org.http4k.connect.kafka.rest

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import org.apache.avro.generic.GenericContainer
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumWriter
import org.http4k.connect.kafka.rest.model.BrokerId
import org.http4k.connect.kafka.rest.model.ConsumerGroup
import org.http4k.connect.kafka.rest.model.ConsumerInstance
import org.http4k.connect.kafka.rest.model.ConsumerRequestTimeout
import org.http4k.connect.kafka.rest.model.Offset
import org.http4k.connect.kafka.rest.model.PartitionId
import org.http4k.connect.kafka.rest.model.SchemaId
import org.http4k.connect.kafka.rest.model.Topic
import org.http4k.connect.model.Base64Blob
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.SimpleMoshiAdapterFactory
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings
import org.http4k.lens.BiDiMapping
import se.ansman.kotshi.KotshiJsonAdapterFactory
import java.io.ByteArrayOutputStream
import java.time.Duration

object KafkaRestMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(KafkaRestJsonAdapterFactory)
        .add(ListAdapter)
        .add(SimpleMoshiAdapterFactory("org.http4k.connect.kafka.rest.model.Records" to { RecordsJsonAdapter(it) }))
        .add(MapAdapter)
        .asConfigurable()
        .withStandardMappings()
        .value(Base64Blob)
        .value(BrokerId)
        .value(ConsumerGroup)
        .value(ConsumerInstance)
        .text(BiDiMapping(ConsumerRequestTimeout::class.java,
            { ConsumerRequestTimeout.of(Duration.ofMillis(it.toLong())) }, { it.value.toMillis().toString() })
        )
        .value(ConsumerInstance)
        .value(Offset)
        .value(PartitionId)
        .value(SchemaId)
        .value(Topic)
        .done()
)

@KotshiJsonAdapterFactory
object KafkaRestJsonAdapterFactory : JsonAdapter.Factory by KotshiKafkaRestJsonAdapterFactory

object GenericRecordAdapter {
    @ToJson
    fun toJson(writer: JsonWriter, value: GenericContainer?) {
        value?.let { writer.jsonValue(KafkaRestMoshi.asA<Map<String, Any>>(it.toAvroData())) } ?: writer.nullValue()
    }

    private fun <T : GenericContainer> T.toAvroData() = ByteArrayOutputStream().let {
        EncoderFactory.get().jsonEncoder(schema, it, false).also {
            SpecificDatumWriter<T>(schema).write(this, it)
            it.flush()
        }
        String(it.toByteArray())
    }
}


