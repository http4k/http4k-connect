package org.http4k.connect.amazon.model

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import org.http4k.connect.amazon.dynamodb.action.AttributeDefinition
import org.http4k.connect.amazon.dynamodb.action.AttributeValue
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.B
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.BOOL
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.BS
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.L
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.M
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.N
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.NS
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.S
import org.http4k.connect.amazon.dynamodb.action.Item
import org.http4k.connect.amazon.dynamodb.action.KeySchema
import org.http4k.connect.amazon.dynamodb.action.KeyType
import org.http4k.lens.BiDiMapping
import org.http4k.lens.Lens
import org.http4k.lens.LensFailure
import org.http4k.lens.LensGet
import org.http4k.lens.LensInjector
import org.http4k.lens.LensSet
import org.http4k.lens.LensSpec
import org.http4k.lens.Meta
import org.http4k.lens.Missing
import org.http4k.lens.ParamMeta.ObjectParam
import org.http4k.lens.StringBiDiMappings
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_TIME
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

/**
 * Constructs typesafe Lenses for the various Item/Key attributes. The lenses
 * can be used to inject values with Item(attr of "123") or extract with attr(item).
 */
class Attribute<FINAL>(
    private val dataType: DynamoDataType,
    meta: Meta,
    get: (Item) -> FINAL,
    private val lensSet: (FINAL, Item) -> Item
) : LensInjector<FINAL, Item>, Lens<Item, FINAL>(meta, get) {

    val name = AttributeName.of(meta.name)

    /**
     * Used for creating tables
     */
    fun asKeySchema(keyType: KeyType) = KeySchema(name, keyType)

    /**
     * Used for creating tables
     */
    fun asAttributeDefinition() = AttributeDefinition(name, dataType)

    /**
     * Return a correctly typed value for this attribute
     */
    fun asValue(value: FINAL): AttributeValue = Item(this of value).getValue(name)

    @Suppress("UNCHECKED_CAST")
    override operator fun <R : Item> invoke(value: FINAL, target: R): R = lensSet(value, target) as R

    override fun toString() = name.toString()

    companion object {
        private fun base(dataType: DynamoDataType) = AttrLensSpec(dataType,
            LensGet { name, target ->
                target[AttributeName.of(name)]
                    ?.takeIf { it.NULL != true }
                    ?.let { listOf(it) }
                    ?: emptyList()
            },

            LensSet { name, values, target ->
                (values.takeIf { it.isNotEmpty() } ?: listOf(AttributeValue.Null()))
                    .fold(target) { m, next -> m + (AttributeName.of(name) to next) }
            }
        )

        fun list() = base(L).map({ it.L!! }, { AttributeValue.List(it) })
        fun map() = base(M).map({ it.M!! }, { AttributeValue.Map(it) })
        fun string() = base(S).map({ it.S!! }, AttributeValue::Str)
        fun strings() = base(L).map({ it.SS!! }, { AttributeValue.StrSet(it) })

        fun nonEmptyString() =
            base(S).map({ it.S!!.takeIf(String::isNotBlank) ?: error("blank string") }, AttributeValue::Str)

        fun int() = base(N).map({ it.N!!.toString().toInt() }, AttributeValue::Num)
        fun numbers() = base(N).map({ it.NS!!.map(String::toBigDecimal).toSet() }, AttributeValue::NumSet)
        fun ints() = base(NS).map({ it.NS!!.map(String::toInt).toSet() }, AttributeValue::NumSet)
        fun long() = base(N).map({ it.N!!.toString().toLong() }, AttributeValue::Num)
        fun longs() = base(NS).map({ it.NS!!.map(String::toLong).toSet() }, AttributeValue::NumSet)
        fun double() = base(N).map({ it.N!!.toString().toDouble() }, AttributeValue::Num)
        fun doubles() = base(NS).map({ it.NS!!.map(String::toDouble).toSet() }, AttributeValue::NumSet)
        fun float() = base(N).map({ it.N!!.toString().toFloat() }, AttributeValue::Num)
        fun floats() = base(NS).map({ it.NS!!.map(String::toFloat).toSet() }, AttributeValue::NumSet)
        fun boolean() = base(BOOL).map({ it.BOOL!! }, AttributeValue::Bool)
        fun base64Blob() = base(B).map({ it.B!! }, { AttributeValue.Base64(it) })
        fun base64Blobs() = base(BS).map({ it.BS!! }, { AttributeValue.Base64Set(it) })
        fun bigDecimal() = base(N).map({ it.N!!.toString().toBigDecimal() }, AttributeValue::Num)
        fun bigDecimals() = base(NS).map({ it.NS!!.map(String::toBigDecimal).toSet() }, AttributeValue::NumSet)
        fun bigInteger() = base(N).map({ it.N!!.toString().toBigInteger() }, AttributeValue::Num)
        fun bigIntegers() = base(NS).map({ it.NS!!.map(String::toBigInteger).toSet() }, AttributeValue::NumSet)
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

        fun timestamp() = long().map(Timestamp::of, Timestamp::value)

        inline fun <reified T : Enum<T>> enum() = string().map(StringBiDiMappings.enum<T>())

        fun <VALUE : Value<T>, T : Any> value(vf: ValueFactory<VALUE, T>) = string().map(vf::parse, vf::show)
    }

    open class AttrLensSpec<OUT>(
        private val dataType: DynamoDataType,
        private val get: LensGet<Item, OUT>,
        private val set: LensSet<Item, OUT>
    ) : LensSpec<Item, OUT>("item", ObjectParam, get) {
        fun <NEXT> map(nextIn: (OUT) -> NEXT, nextOut: (NEXT) -> OUT) =
            AttrLensSpec(dataType, get.map(nextIn), set.map(nextOut))

        fun <NEXT> map(mapping: BiDiMapping<OUT, NEXT>) = map(mapping::invoke, mapping::invoke)

        override fun optional(name: String, description: String?): Attribute<OUT?> {
            val getLens = get(name)
            val setLens = set(name)
            return Attribute(
                dataType,
                Meta(false, location, paramMeta, name, description),
                { getLens(it).run { if (isEmpty()) null else first() } },
                { out: OUT?, target -> setLens(out?.let { listOf(it) } ?: emptyList(), target) }
            )
        }

        override fun required(name: String, description: String?): Attribute<OUT> {
            val getLens = get(name)
            val setLens = set(name)
            return Attribute(
                dataType,
                Meta(true, location, paramMeta, name, description),
                {
                    getLens(it).firstOrNull()
                        ?: throw LensFailure(Missing(Meta(true, location, paramMeta, name, description)), target = it)
                },
                { out: OUT, target -> setLens(listOf(out), target) })
        }
    }
}
