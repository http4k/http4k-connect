package org.http4k.format

import dev.forkhandles.values.Value
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.lens.BiDiMapping

inline fun <reified V : Value<String>, T> AutoMappingConfiguration<T>.text(noinline fn: (String) -> V) = text(BiDiMapping(fn) { it.value })

inline fun <reified V : Value<Long>, T> AutoMappingConfiguration<T>.long(noinline fn: (Long) -> V) = long(BiDiMapping(fn) { it.value })

fun <T> AutoMappingConfiguration<T>.withAwsCoreMappings() = apply {
    text(ARN::of)
    text(AwsService::of)
    text(AwsAccount::parse)
    text(Base64Blob::of)
    text(KMSKeyId::of)
    text(Region::of)
    double(BiDiMapping({ Timestamp.of(it.toLong()) }, { it.value.toDouble() }))
}
