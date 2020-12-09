package org.http4k.connect.amazon.secretsmanager

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.text
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object SecretsManagerMoshi : ConfigurableMoshi(Moshi.Builder()
    .add(Unit::class.java, UnitAdapter)
    .asConfigurable()
    .withStandardMappings()
    .withAwsCoreMappings()
    .text(SecretId::of)
    .text(VersionId::of)
    .text(VersionStage::of)
    .done()
)

private object UnitAdapter : JsonAdapter<Unit>() {
    override fun fromJson(reader: JsonReader) {
        reader.readJsonValue(); Unit
    }

    override fun toJson(writer: JsonWriter, value: Unit?) {
        value?.let { writer.beginObject().endObject() } ?: writer.nullValue()
    }
}
