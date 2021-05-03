package org.http4k.connect.amazon.dynamodb.grammar

import dev.forkhandles.tuples.val1
import dev.forkhandles.tuples.val3
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.DynamoDataType
import parser4k.Parser
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.Tokens.identifier
import parser4k.commonparsers.Tokens.number
import parser4k.commonparsers.token
import parser4k.inOrder
import parser4k.map
import parser4k.oneOf
import parser4k.ref
import parser4k.skipFirst
import parser4k.skipWrapper

interface Expr {
    fun eval(item: ItemWithSubstitutions): Any
}

data class Equal(val attr1: Expr, val attr2: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        attr1.eval(item) == attr2.eval(item)

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class NotEqual(val attr1: Expr, val attr2: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        attr1.eval(item) != attr2.eval(item)

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class LessThan(val attr1: Expr, val attr2: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        (asString(attr1.eval(item)).toString().padStart(200)) < (asString(attr2.eval(item)).toString()
            .padStart(200))

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class LessThanOrEqual(val attr1: Expr, val attr2: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        (asString(attr1.eval(item)).toString().padStart(200)) <= (asString(attr2.eval(item)).toString()
            .padStart(200))

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class GreaterThan(val attr1: Expr, val attr2: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        (asString(attr1.eval(item)).toString().padStart(200)) > (asString(attr2.eval(item)).toString()
            .padStart(200))

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class GreaterThanOrEqual(val attr1: Expr, val attr2: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        (asString(attr1.eval(item)).toString().padStart(200)) >= (asString(attr2.eval(item)).toString()
            .padStart(200))

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class Size(val attr: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) = AttributeValue.Num(
        asString(attr.eval(item))
            .takeIf { it != NULLMARKER }.toString().length
    )

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class In(val value: Expr, val values: List<Expr>) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        values.map { it.eval(item) }.contains(value.eval(item))

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class AttributeExists(val attr: AttributeName) : Expr {
    override fun eval(item: ItemWithSubstitutions) = item.item.containsKey(attr)

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class AttributeNotExists(val attr: AttributeName) : Expr {
    override fun eval(item: ItemWithSubstitutions) = !item.item.containsKey(attr)

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class AttributeType(val attr: Expr, val dynamoDataType: DynamoDataType) : Expr {
    override fun eval(item: ItemWithSubstitutions) = attr.eval(item).let {
        AttributeValue::class.java.methods.find { it.name == "get" + dynamoDataType.name }?.invoke(it) != null
    }

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> =
            inOrder(
                token("attribute_type"),
                token("("),
                ref(parser),
                token(","),
                identifier,
                token(")")
            )
                .skipWrapper()
                .map { AttributeType(it.val2, DynamoDataType.valueOf(it.val4)) }

    }
}

data class BeginsWith(val attr: Expr, val value: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        (asString(attr.eval(item)).toString()).startsWith(
            asString(value.eval(item)).toString()
        )

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> =
            inOrder(token("begins_with"), token("("), ref(parser), token(","), ref(parser), token(")"))
                .map { BeginsWith(it.val3, it.val5) }
    }
}

data class Contains(val attr: Expr, val value: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        when (val av = asString(attr.eval(item))) {
            is String -> setOf(av)
            is Set<*> -> av
            else -> error("can't compare $av")
        }.contains(asString(value.eval(item)).toString())

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> =
            inOrder(token("contains"), token("("), ref(parser), token(","), ref(parser), token(")"))
                .skipWrapper()
                .map { Contains(it.val2, it.val4) }
    }
}

data class And(val left: Expr, val right: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions): Boolean =
        (left.eval(item) as Boolean) && (right.eval(item) as Boolean)

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class Or(val left: Expr, val right: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        (left.eval(item) as Boolean) || (right.eval(item) as Boolean)

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class Not(val expr: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        !(expr.eval(item) as Boolean)

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = TODO()
    }
}

data class ConditionAttributeValue(val value: String) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        item.item[AttributeName.of(value)] ?: AttributeValue.Null()

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = Tokens.identifier.map(::ConditionAttributeValue)
    }
}

data class IndexedAttributeValue(val expr: Expr, val index: Int) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        (expr.eval(item) as AttributeValue).L?.get(index) ?: AttributeValue.Null()

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> =
            inOrder(ref(parser), token("["), number, token("]"))
                .map { IndexedAttributeValue(it.val1, it.val3.toInt()) }
    }
}

data class MapAttributeValue(val parent: Expr, val child: Expr) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        child.eval(item.copy(item = (parent.eval(item) as AttributeValue).M ?: emptyMap()))

    companion object {
        fun parser(parser: () -> Parser<Expr>) = inOrder(ref(parser), token("."), ref(parser))
            .map { MapAttributeValue(it.val1, it.val3) }
    }
}

data class ExpressionAttributeValue(val value: String) : Expr {
    override fun eval(item: ItemWithSubstitutions) =
        item.values[value] ?: error("missing value $value")

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = inOrder(oneOf(':'), identifier)
            .skipFirst().map(::ExpressionAttributeValue)
    }
}

data class ExpressionAttributeName(val value: String) : Expr {
    override fun eval(item: ItemWithSubstitutions): AttributeValue =
        (item.names[value] ?: error("missing name $value")).let {
            ConditionAttributeValue(it.value).eval(item)
        }

    companion object {
        fun parser(parser: () -> Parser<Expr>): Parser<Expr> = inOrder(oneOf('#'), Tokens.identifier)
            .skipFirst().map(::ExpressionAttributeName)
    }
}

private const val NULLMARKER = "__*NULL*__"

private fun asString(attributeValue: Any): Any =
    with(attributeValue as AttributeValue) {
        when {
            B != null -> B!!.value
            BOOL != null -> BOOL!!.toString()
            BS != null -> BS!!.map { it.value }
            L != null -> L!!.map(::asString)
            M != null -> M!!.map { asString(it.value) }
            N != null -> N!!
            NS != null -> NS!!
            S != null -> S!!
            SS != null -> SS!!
            else -> NULLMARKER
        }
    }
