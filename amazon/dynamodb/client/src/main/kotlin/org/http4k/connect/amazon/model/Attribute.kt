package org.http4k.connect.amazon.model

import org.http4k.connect.amazon.dynamodb.action.AttributeValue
import org.http4k.connect.amazon.dynamodb.action.ItemAttributes
import org.http4k.lens.BiDiLensSpec
import org.http4k.lens.BiDiMapping
import org.http4k.lens.LensGet
import org.http4k.lens.LensSet
import org.http4k.lens.ParamMeta.ObjectParam
import org.http4k.lens.StringBiDiMappings
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_TIME
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

object Attribute {

    private val base = AttrLensSpec(
        LensGet { name, target ->
            target[AttributeName.of(name)]?.takeIf { it.NULL != true }?.let { listOf(it) } ?: emptyList() },
        LensSet { name, values, target ->
            (values.takeIf { it.isNotEmpty() } ?: listOf(AttributeValue.Null()))
                .fold(target) { m, next -> m + (AttributeName.of(name) to next) }
        }
    )

    fun list() = base.map({ it.L!! }, { AttributeValue.List(it) })
    fun map() = base.map({ it.M!! }, { AttributeValue.Map(it) })
    fun string() = base.map({ it.S!! }, AttributeValue.Companion::Str)
    fun strings() = base.map({ it.SS!! }, { AttributeValue.StrSet(it) })
    fun nonEmptyString() =
        base.map({ it.S!!.takeIf(String::isNotBlank) ?: error("blank string") }, AttributeValue.Companion::Str)

    fun int() = base.map({ it.N!!.toString().toInt() }, AttributeValue.Companion::Num)
    fun numbers() = base.map({ it.NS!!.map(String::toBigDecimal).toSet() }, AttributeValue.Companion::NumSet)
    fun ints() = base.map({ it.NS!!.map(String::toInt).toSet() }, AttributeValue.Companion::NumSet)
    fun long() = base.map({ it.N!!.toString().toLong() }, AttributeValue.Companion::Num)
    fun longs() = base.map({ it.NS!!.map(String::toLong).toSet() }, AttributeValue.Companion::NumSet)
    fun double() = base.map({ it.N!!.toString().toDouble() }, AttributeValue.Companion::Num)
    fun doubles() = base.map({ it.NS!!.map(String::toDouble).toSet() }, AttributeValue.Companion::NumSet)
    fun float() = base.map({ it.N!!.toString().toFloat() }, AttributeValue.Companion::Num)
    fun floats() = base.map({ it.NS!!.map(String::toFloat).toSet() }, AttributeValue.Companion::NumSet)
    fun boolean() = base.map({ it.BOOL!! }, AttributeValue.Companion::Bool)
    fun base64Blob() = base.map({ it.B!! }, { AttributeValue.Base64(it) })
    fun base64Blobs() = base.map({ it.BS!! }, { AttributeValue.Base64Set(it) })
    fun bigDecimal() = base.map({ it.N!!.toString().toBigDecimal() }, AttributeValue.Companion::Num)
    fun bigDecimals() = base.map({ it.NS!!.map(String::toBigDecimal).toSet() }, AttributeValue.Companion::NumSet)
    fun bigInteger() = base.map({ it.N!!.toString().toBigInteger() }, AttributeValue.Companion::Num)
    fun bigIntegers() = base.map({ it.NS!!.map(String::toBigInteger).toSet() }, AttributeValue.Companion::NumSet)
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

fun <NEXT, IN : Any, OUT> BiDiLensSpec<IN, OUT>.map(mapping: BiDiMapping<OUT, NEXT>) =
    map(mapping::invoke, mapping::invoke)

open class AttrLensSpec<OUT>(
    get: LensGet<ItemAttributes, OUT>,
    setter: LensSet<ItemAttributes, OUT>
) : BiDiLensSpec<ItemAttributes, OUT>("item", ObjectParam, get, setter) {
    override val multi get() = throw UnsupportedOperationException("use other methods")
}
