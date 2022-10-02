package org.http4k.connect.amazon.dynamodb.model

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.dynamodb.model.DynamoDataType.valueOf
import org.http4k.core.Uri
import org.http4k.lens.BiDiMapping
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZonedDateTime
import java.util.UUID

typealias TokensToNames = Map<String, AttributeName>
typealias TokensToValues = Map<String, AttributeValue>
typealias ItemResult = Map<String, Map<String, Any>>

fun ItemResult.toItem() =
    map {
        val (key, v) = it.value.entries.first()
        AttributeName.of(it.key) to AttributeValue.from(valueOf(key), v)
    }
        .toMap()

/**
 * Used for creating tables
 */
fun <T> Attribute<T>.asKeySchema(keyType: KeyType) = KeySchema(name, keyType)

/**
 * Used for creating tables
 */
fun <T> Attribute<T>.asAttributeDefinition() = AttributeDefinition(name, dataType)

/**
 * Map items out of a collection
 */
fun <T> Attribute.AttrLensSpec<List<AttributeValue>>.map(next: BiDiMapping<Item, T>): Attribute.AttrLensSpec<List<T>> = map(
    { it.mapNotNull { it.M }.map { next(it) } },
    { it.map { AttributeValue.Map(next(it)) } },
)

/**
 * Map items out of a collection
 */
fun <T> Attribute.Companion.list(next: BiDiMapping<Item, T>) = list().map(
    { it.mapNotNull { it.M }.map { next(it) } },
    { it.map { AttributeValue.Map(next(it)) } },
)

/**
 * Map items out of a collection
 */
fun <T> Attribute.Companion.map(next: BiDiMapping<Item, T>) = map().map(next)

fun <P : Any, VALUE : Value<P>> Attribute.AttrLensSpec<P>.value(vf: ValueFactory<VALUE, P>) = map(vf::of, vf::unwrap)

@JvmName("valueString")
fun <VALUE : Value<String>> Attribute.Companion.value(vf: ValueFactory<VALUE, String>) = string().value(vf)

@JvmName("valueBoolean")
fun <VALUE : Value<Boolean>> Attribute.Companion.value(vf: ValueFactory<VALUE, Boolean>) = boolean().value(vf)

@JvmName("valueLong")
fun <VALUE : Value<Long>> Attribute.Companion.value(vf: ValueFactory<VALUE, Long>) = long().value(vf)

@JvmName("valueInt")
fun <VALUE : Value<Int>> Attribute.Companion.value(vf: ValueFactory<VALUE, Int>) = int().value(vf)

@JvmName("valueDouble")
fun <VALUE : Value<Double>> Attribute.Companion.value(vf: ValueFactory<VALUE, Double>) = double().value(vf)

@JvmName("valueFloat")
fun <VALUE : Value<Float>> Attribute.Companion.value(vf: ValueFactory<VALUE, Float>) = float().value(vf)

@JvmName("valueBigInteger")
fun <VALUE : Value<BigInteger>> Attribute.Companion.value(vf: ValueFactory<VALUE, BigInteger>) = bigInteger().value(vf)

@JvmName("valueBigDecimal")
fun <VALUE : Value<BigDecimal>> Attribute.Companion.value(vf: ValueFactory<VALUE, BigDecimal>) = bigDecimal().value(vf)

@JvmName("valueUri")
fun <VALUE : Value<Uri>> Attribute.Companion.value(vf: ValueFactory<VALUE, Uri>) = uri().value(vf)

@JvmName("valueUUID")
fun <VALUE : Value<UUID>> Attribute.Companion.value(vf: ValueFactory<VALUE, UUID>) = uuid().value(vf)

@JvmName("valueDuration")
fun <VALUE : Value<Duration>> Attribute.Companion.value(vf: ValueFactory<VALUE, Duration>) = duration().value(vf)

@JvmName("valueInstant")
fun <VALUE : Value<Instant>> Attribute.Companion.value(vf: ValueFactory<VALUE, Instant>) = instant().value(vf)

@JvmName("valueZonedDateTime")
fun <VALUE : Value<ZonedDateTime>> Attribute.Companion.value(vf: ValueFactory<VALUE, ZonedDateTime>) = zonedDateTime().value(vf)

@JvmName("valueLocalDate")
fun <VALUE : Value<LocalDate>> Attribute.Companion.value(vf: ValueFactory<VALUE, LocalDate>) = localDate().value(vf)

@JvmName("valueLocalDateTime")
fun <VALUE : Value<LocalDateTime>> Attribute.Companion.value(vf: ValueFactory<VALUE, LocalDateTime>) = localDateTime().value(vf)

@JvmName("valueLocalTime")
fun <VALUE : Value<LocalTime>> Attribute.Companion.value(vf: ValueFactory<VALUE, LocalTime>) = localTime().value(vf)

@JvmName("valueOffsetTime")
fun <VALUE : Value<OffsetTime>> Attribute.Companion.value(vf: ValueFactory<VALUE, OffsetTime>) = offsetTime().value(vf)

@JvmName("valueOffsetDateTime")
fun <VALUE : Value<OffsetDateTime>> Attribute.Companion.value(vf: ValueFactory<VALUE, OffsetDateTime>) = offsetDateTime().value(vf)

@JvmName("valueTimestamp")
fun <VALUE : Value<Timestamp>> Attribute.Companion.value(vf: ValueFactory<VALUE, Timestamp>) = timestamp().value(vf)

