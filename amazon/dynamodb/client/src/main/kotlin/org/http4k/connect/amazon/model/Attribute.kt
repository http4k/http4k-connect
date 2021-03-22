package org.http4k.connect.amazon.model

import org.http4k.connect.amazon.dynamodb.action.AttributeValue
import org.http4k.connect.amazon.dynamodb.action.ItemAttributes
import org.http4k.lens.BiDiLensSpec
import org.http4k.lens.LensGet
import org.http4k.lens.LensSet
import org.http4k.lens.ParamMeta.ObjectParam
import org.http4k.lens.StringBiDiMappings
import org.http4k.lens.map
import java.time.format.DateTimeFormatter

object Attribute : AttrLensSpec<AttributeValue>(
    LensGet { name, target -> target[AttributeName.of(name)]?.let { listOf(it) } ?: emptyList() },
    LensSet { name, values, target ->
        (values.takeIf { it.isNotEmpty() } ?: listOf(AttributeValue.Null()))
            .fold(target) { m, next -> m + (AttributeName.of(name) to next) }
    }
) {
    fun list() = map({ it.L!! }, { AttributeValue.List(it) })
    fun map() = map({ it.M!! }, { AttributeValue.Map(it) })
    fun string() = map({ it.S!! }, AttributeValue.Companion::Str)
    fun strings() = map({ it.SS!! }, { AttributeValue.StrSet(it) })
    fun nonEmptyString() =
        this.map({ it.S!!.takeIf(String::isNotBlank) ?: error("blank string") }, AttributeValue.Companion::Str)

    fun int() = this.map({ it.N!!.toString().toInt() }, AttributeValue.Companion::Num)
    fun numbers() = this.map({ it.NS!!.map(String::toBigDecimal).toSet() }, AttributeValue.Companion::NumSet)
    fun ints() = this.map({ it.NS!!.map(String::toInt).toSet() }, AttributeValue.Companion::NumSet)
    fun long() = this.map({ it.N!!.toString().toLong() }, AttributeValue.Companion::Num)
    fun longs() = this.map({ it.NS!!.map(String::toLong).toSet() }, AttributeValue.Companion::NumSet)
    fun double() = this.map({ it.N!!.toString().toDouble() }, AttributeValue.Companion::Num)
    fun doubles() = this.map({ it.NS!!.map(String::toDouble).toSet() }, AttributeValue.Companion::NumSet)
    fun float() = this.map({ it.N!!.toString().toFloat() }, AttributeValue.Companion::Num)
    fun floats() = this.map({ it.NS!!.map(String::toFloat).toSet() }, AttributeValue.Companion::NumSet)
    fun boolean() = this.map({ it.BOOL!! }, AttributeValue.Companion::Bool)
    fun base64Blob() = this.map({ it.B!! }, { AttributeValue.Base64(it) })
    fun base64Blobs() = this.map({ it.BS!! }, { AttributeValue.Base64Set(it) })
    fun bigDecimal() = this.map({ it.N!!.toString().toBigDecimal() }, AttributeValue.Companion::Num)
    fun bigDecimals() = this.map({ it.NS!!.map(String::toBigDecimal).toSet() }, AttributeValue.Companion::NumSet)
    fun bigInteger() = this.map({ it.N!!.toString().toBigInteger() }, AttributeValue.Companion::Num)
    fun bigIntegers() = this.map({ it.NS!!.map(String::toBigInteger).toSet() }, AttributeValue.Companion::NumSet)
    fun uuid() = string().map(StringBiDiMappings.uuid())
    fun uri() = string().map(StringBiDiMappings.uri())
    fun duration() = string().map(StringBiDiMappings.duration())
    fun yearMonth() = string().map(StringBiDiMappings.yearMonth())
    fun instant() = string().map(StringBiDiMappings.instant())
    fun localDateTime(formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME) =
        string().map(StringBiDiMappings.localDateTime(formatter))

    fun zonedDateTime(formatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME) =
        string().map(StringBiDiMappings.zonedDateTime(formatter))

    fun localDate(formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE) =
        string().map(StringBiDiMappings.localDate(formatter))

    fun localTime(formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME) =
        string().map(StringBiDiMappings.localTime(formatter))

    fun offsetTime(formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_TIME) =
        string().map(StringBiDiMappings.offsetTime(formatter))

    fun offsetDateTime(formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME) =
        string().map(StringBiDiMappings.zonedDateTime(formatter))

    inline fun <reified T : Enum<T>> enum() = string().map(StringBiDiMappings.enum<T>())
}

open class AttrLensSpec<OUT>(
    get: LensGet<ItemAttributes, OUT>,
    setter: LensSet<ItemAttributes, OUT>
) : BiDiLensSpec<ItemAttributes, OUT>("item", ObjectParam, get, setter) {
    override val multi get() = throw UnsupportedOperationException("use other methods")
}
