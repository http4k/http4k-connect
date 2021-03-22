package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.regex
import org.http4k.connect.amazon.dynamodb.action.AttributeValue
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Null
import org.http4k.connect.amazon.dynamodb.action.ItemAttributes
import org.http4k.connect.amazon.dynamodb.action.KeySchema
import org.http4k.connect.amazon.model.DynamoDataType.B
import org.http4k.connect.amazon.model.DynamoDataType.BOOL
import org.http4k.connect.amazon.model.DynamoDataType.BS
import org.http4k.connect.amazon.model.DynamoDataType.L
import org.http4k.connect.amazon.model.DynamoDataType.M
import org.http4k.connect.amazon.model.DynamoDataType.N
import org.http4k.connect.amazon.model.DynamoDataType.NS
import org.http4k.connect.amazon.model.DynamoDataType.S
import org.http4k.connect.amazon.model.DynamoDataType.SS
import org.http4k.lens.BiDiLens
import org.http4k.lens.BiDiLensSpec
import org.http4k.lens.LensExtractor
import org.http4k.lens.LensGet
import org.http4k.lens.LensSet
import org.http4k.lens.ParamMeta.StringParam
import org.http4k.lens.StringBiDiMappings
import org.http4k.lens.map
import se.ansman.kotshi.JsonSerializable
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_TIME
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

fun Item(): ItemAttributes = mapOf()

open class AttrLensSpec<OUT>(
    protected val dataType: DynamoDataType,
    private val get: LensGet<ItemAttributes, OUT>,
    private val setter: LensSet<ItemAttributes, OUT>
) : BiDiLensSpec<ItemAttributes, OUT>("item", StringParam, get, setter) {
    internal fun with(dataType: DynamoDataType) = AttrLensSpec(dataType, get, setter)

    override val multi = throw UnsupportedOperationException("")
}

object Attr : AttrLensSpec<AttributeValue>(S,
    LensGet { name, target -> target[AttributeName.of(name)]?.let { listOf(it) } ?: emptyList() },
    LensSet { name, values, target ->
        (values.takeIf { it.isNotEmpty() } ?: listOf(Null()))
            .fold(target) { m, next -> m + (AttributeName.of(name) to next) }
    }
) {
    fun string() = map({ it.S!! }, AttributeValue::Str)
    fun strings() = map({ it.SS!! }, { AttributeValue.StrSet(it) })
    fun nonEmptyString() =
        with(N).map({ it.S!!.takeIf(String::isNotBlank) ?: error("blank string") }, AttributeValue::Str)
    fun int() = with(N).map({ it.N!!.toString().toInt() }, AttributeValue::Num)
    fun ints() = with(NS).map({ it.NS!!.map(String::toInt).toSet() }, AttributeValue::NumSet)
    fun long() = with(N).map({ it.N!!.toString().toLong() }, AttributeValue::Num)
    fun longs() = with(NS).map({ it.NS!!.map(String::toLong).toSet() }, AttributeValue::NumSet)
    fun double() = with(N).map({ it.N!!.toString().toDouble() }, AttributeValue::Num)
    fun doubles() = with(NS).map({ it.NS!!.map(String::toDouble).toSet() }, AttributeValue::NumSet)
    fun float() = with(N).map({ it.N!!.toString().toFloat() }, AttributeValue::Num)
    fun floats() = with(NS).map({ it.NS!!.map(String::toFloat).toSet() }, AttributeValue::NumSet)
    fun boolean() = with(BOOL).map({ it.BOOL!! }, AttributeValue::Bool)
    fun base64Blob() = with(B).map({ it.B!! }, { AttributeValue.Base64(it) })
    fun base64Blobs() = with(BS).map({ it.BS!! }, { AttributeValue.Base64Set(it) })
    fun bigDecimal() = with(N).map({ it.N!!.toString().toBigDecimal() }, AttributeValue::Num)
    fun bigDecimals() = with(NS).map({ it.NS!!.map(String::toBigDecimal).toSet() }, AttributeValue::NumSet)
    fun bigInteger() = with(N).map({ it.N!!.toString().toBigInteger() }, AttributeValue::Num)
    fun bigIntegers() = with(NS).map({ it.NS!!.map(String::toBigInteger).toSet() }, AttributeValue::NumSet)
    fun uuid() = string().map(StringBiDiMappings.uuid())
    fun uri() = string().map(StringBiDiMappings.uri())
    fun duration() = string().map(StringBiDiMappings.duration())
    fun yearMonth() = string().map(StringBiDiMappings.yearMonth())
    fun instant() = string().map(StringBiDiMappings.instant())
    fun localDateTime(formatter: DateTimeFormatter = ISO_LOCAL_DATE_TIME) =
        string().map(StringBiDiMappings.localDateTime(formatter))

    fun zonedDateTime(formatter: DateTimeFormatter = ISO_ZONED_DATE_TIME) =
        string().map(StringBiDiMappings.zonedDateTime(formatter))

    fun localDate(formatter: DateTimeFormatter = ISO_LOCAL_DATE) =
        string().map(StringBiDiMappings.localDate(formatter))

    fun localTime(formatter: DateTimeFormatter = ISO_LOCAL_TIME) =
        string().map(StringBiDiMappings.localTime(formatter))

    fun offsetTime(formatter: DateTimeFormatter = ISO_OFFSET_TIME) =
        string().map(StringBiDiMappings.offsetTime(formatter))

    fun offsetDateTime(formatter: DateTimeFormatter = ISO_OFFSET_DATE_TIME) =
        string().map(StringBiDiMappings.zonedDateTime(formatter))

    inline fun <reified T : Enum<T>> enum() = string().map(StringBiDiMappings.enum<T>())
}

val <FINAL> BiDiLens<ItemAttributes, FINAL>.name get() = meta.name

data class Attribute<IN, OUT>(
    val name: AttributeName,
    val type: DynamoDataType,
    private val toVal: (IN) -> AttributeValue,
    private val fromValue: (AttributeValue) -> OUT?
) : LensExtractor<ItemAttributes, OUT?> {
    /**
     * Create a typed binding for this attribute
     */
    infix fun to(t: IN) = name to toVal(t)

    /**
     * Used for creating tables
     */
    fun keySchema(keyType: KeyType) = KeySchema(name, keyType)

    /**
     * Used for creating tables
     */
    fun attrDefinition() = AttributeDefinition(name, type)

    override fun toString() = name.toString()

    /**
     * Lookup this attribute from a queried Item
     */
    override fun invoke(target: ItemAttributes) = target[name]?.let { fromValue(it) }

    companion object {
        fun boolean(name: String) = Attribute(AttributeName.of(name), BOOL, AttributeValue::Bool, AttributeValue::BOOL)
        fun base64Blob(name: String) = Attribute(AttributeName.of(name), B, AttributeValue::Base64, AttributeValue::B)
        fun base64Blobs(name: String) =
            Attribute(AttributeName.of(name), BS, AttributeValue::Base64Set, AttributeValue::BS)

        fun list(name: String) = Attribute(AttributeName.of(name), L, AttributeValue::List, AttributeValue::L)
        fun map(name: String) = Attribute(AttributeName.of(name), M, AttributeValue::Map, AttributeValue::M)
        fun number(name: String) = Attribute(AttributeName.of(name), N, AttributeValue::Num) { BigDecimal(it.N) }
        fun numbers(name: String) =
            Attribute(AttributeName.of(name), NS, AttributeValue::NumSet) { it.NS?.map(::BigDecimal)?.toSet() }

        fun string(name: String) = Attribute(AttributeName.of(name), S, AttributeValue::Str, AttributeValue::S)
        fun strings(name: String) = Attribute(AttributeName.of(name), SS, AttributeValue::StrSet, AttributeValue::SS)
    }
}

class AttributeName private constructor(value: String) : StringValue(value), Comparable<AttributeName> {
    companion object : NonBlankStringValueFactory<AttributeName>(::AttributeName)

    override fun compareTo(other: AttributeName): Int = value.compareTo(other.value)
}

enum class DynamoDataType {
    B, BOOL, BS, L, M, N, NS, NULL, S, SS
}

enum class BillingMode {
    PROVISIONED, PAY_PER_REQUEST
}

class IndexName private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<IndexName>(::IndexName)
}

class TableName private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<TableName>(::TableName, "[a-zA-Z0-9_.-]+".regex)
}

@JsonSerializable
data class AttributeDefinition(
    val AttributeName: AttributeName,
    val AttributeType: DynamoDataType
)

enum class KeyType {
    HASH, RANGE
}

enum class ProjectionType {
    ALL, KEYS_ONLY, INCLUDE
}

enum class SSEType {
    AES256, KMS
}

enum class StreamViewType {
    NEW_IMAGE, OLD_IMAGE, NEW_AND_OLD_IMAGES, KEYS_ONLY
}

enum class IndexStatus {
    CREATING, UPDATING, DELETING, ACTIVE
}

enum class ReplicaStatus {
    CREATING, CREATION_FAILED, UPDATING, DELETING, ACTIVE, REGION_DISABLED, INACCESSIBLE_ENCRYPTION_CREDENTIALS
}

enum class TableStatus {
    CREATING, UPDATING, DELETING, ACTIVE, INACCESSIBLE_ENCRYPTION_CREDENTIALS, ARCHIVING, ARCHIVED
}
