package org.http4k.connect.amazon.dynamodb.grammar

import dev.forkhandles.tuples.val2
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.And
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.AttributeExists
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.AttributeNotExists
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.AttributeType
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.BeginsWith
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.Between
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.ConditionAttributeValue
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.Contains
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.Equal
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.ExpressionAttributeValue
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.GreaterThan
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.GreaterThanOrEqual
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.In
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.LessThan
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.LessThanOrEqual
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.Not
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.NotEqual
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.Or
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbGrammar.Expr.Size
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.AttributeValue.Companion.Null
import org.http4k.connect.amazon.dynamodb.model.DynamoDataType
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.TokensToValues
import parser4k.OutputCache
import parser4k.Parser
import parser4k.asBinary
import parser4k.commonparsers.Tokens.identifier
import parser4k.commonparsers.token
import parser4k.inOrder
import parser4k.map
import parser4k.mapLeftAssoc
import parser4k.nestedPrecedence
import parser4k.oneOf
import parser4k.oneOfWithPrecedence
import parser4k.parseWith
import parser4k.ref
import parser4k.reset
import parser4k.skipFirst
import parser4k.skipWrapper
import parser4k.with

object DynamoDbGrammar {
    private val cache = OutputCache<Expr>()

    private fun binaryExpr(tokenString: String, f: (Expr, Expr) -> Expr) =
        inOrder(ref { expr }, token(tokenString), ref { expr }).mapLeftAssoc(f.asBinary()).with(cache)

    private fun unaryExpr(tokenString: String, f: (Expr) -> Expr) =
        inOrder(token(tokenString), ref { expr }).map { (_, it) -> f(it) }.with(cache)

    private val equal = binaryExpr("=", ::Equal)
    private val notEqual = binaryExpr("<>", ::NotEqual)
    private val lessThan = binaryExpr("<", ::LessThan)
    private val greaterThan = binaryExpr(">", ::GreaterThan)
    private val lessThanOrEqual = binaryExpr("<=", ::LessThanOrEqual)
    private val greaterThanOrEqual = binaryExpr(">=", ::GreaterThanOrEqual)

    private val `in` = unaryExpr("IN", ::In)
    private val `size` = unaryExpr("size", ::Size)

    private val between = inOrder(ref { expr }, token("BETWEEN"), ref { expr }, token("AND"), ref { expr })
        .map { Between(it.val1, it.val3, it.val5) }.with(cache)

    private val attributeExists = inOrder(token("attribute_exists"), token("("), identifier, token(")"))
        .skipWrapper()
        .map { AttributeExists(AttributeName.of(it.val2)) }.with(cache)

    private val attributeNotExists = inOrder(token("attribute_not_exists"), token("("), identifier, token(")"))
        .skipWrapper()
        .map { AttributeNotExists(AttributeName.of(it.val2)) }.with(cache)

    private val attributeType =
        inOrder(token("attribute_type"), token("("), ref { expr }, token(","), identifier, token(")"))
            .skipWrapper()
            .map { AttributeType(it.val2, DynamoDataType.valueOf(it.val4)) }.with(cache)

    private val beginsWith =
        inOrder(token("begins_with"), token("("), ref { expr }, token(","), ref { expr }, token(")"))
            .map { BeginsWith(it.val3, it.val5) }.with(cache)

    private val contains = inOrder(token("contains"), token("("), ref { expr }, token(","), ref { expr }, token(")"))
        .skipWrapper()
        .map { Contains(it.val2, it.val4) }.with(cache)

    private val paren = inOrder(token("("), ref { expr }, token(")")).skipWrapper().with(cache)
    private val not = unaryExpr("NOT", ::Not)
    private val and =
        inOrder(ref { expr }, token("AND"), ref { expr }).mapLeftAssoc({ left: Expr, right: Expr ->
            And(left, right)
        }.asBinary()).with(cache)
    private val or =
        inOrder(ref { expr }, token("OR"), ref { expr }).mapLeftAssoc({ left: Expr, right: Expr ->
            Or(
                left,
                right
            )
        }.asBinary<Expr, Expr, Expr>()).with(cache)

    private val attributeName = identifier.map(::ConditionAttributeValue).with(cache)

    private val expressionAttributeValue = inOrder(oneOf(':'), identifier)
        .skipFirst().map { ExpressionAttributeValue(it) }
        .with(cache)

    operator fun invoke(expression: String): Expr = expression.parseWith(expr)

    private val expr: Parser<Expr> = oneOfWithPrecedence(
        between,
        not,
        and,
        or,
        oneOf(equal, notEqual, lessThan, lessThanOrEqual, greaterThan, greaterThanOrEqual),
        oneOf(`in`, size),
        oneOf(attributeExists, attributeNotExists, attributeType, beginsWith, contains),
        paren.nestedPrecedence(),
        oneOfWithPrecedence(expressionAttributeValue, attributeName)
    ).reset(cache)

    sealed class Expr {
        abstract fun eval(item: Item, values: TokensToValues): Any

        data class Equal(val attr1: Expr, val attr2: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                attr1.eval(item, values) == attr2.eval(item, values)
        }

        data class NotEqual(val attr1: Expr, val attr2: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                attr1.eval(item, values) != attr2.eval(item, values)
        }

        data class LessThan(val attr1: Expr, val attr2: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                (asString(attr1.eval(item, values)).toString()) < (asString(attr2.eval(item, values)).toString())
        }

        data class LessThanOrEqual(val attr1: Expr, val attr2: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                (asString(attr1.eval(item, values)).toString()) <= (asString(attr2.eval(item, values)).toString())
        }

        data class GreaterThan(val attr1: Expr, val attr2: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                (asString(attr1.eval(item, values)).toString()) > (asString(attr2.eval(item, values)).toString())
        }

        data class GreaterThanOrEqual(val attr1: Expr, val attr2: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                (asString(attr1.eval(item, values)).toString()) >= (asString(attr2.eval(item, values)).toString())
        }

        data class Size(val attr: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) = AttributeValue.Num(
                asString(attr.eval(item, values))
                    .takeIf { it != NULLMARKER }.toString().length
            )
        }

        data class In(val value: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) = TODO()
        }

        data class Between(val attr: Expr, val startAttr: Expr, val endAttr: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) = TODO()
        }

        data class AttributeExists(val attr: AttributeName) : Expr() {
            override fun eval(item: Item, values: TokensToValues) = item.containsKey(attr)
        }

        data class AttributeNotExists(val attr: AttributeName) : Expr() {
            override fun eval(item: Item, values: TokensToValues) = !item.containsKey(attr)
        }

        data class AttributeType(val attr: Expr, val dynamoDataType: DynamoDataType) : Expr() {
            override fun eval(item: Item, values: TokensToValues) = attr.eval(item, values)?.let {
                AttributeValue::class.java.methods.find { it.name == "get" + dynamoDataType.name }?.invoke(it) != null
            }
        }

        data class BeginsWith(val attr: Expr, val value: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                (asString(attr.eval(item, values)).toString()).startsWith(
                    asString(value.eval(item, values)).toString()
                )
        }

        data class Contains(val attr: Expr, val value: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                when (val av = asString(attr.eval(item, values))) {
                    is String -> setOf(av)
                    is Set<*> -> av
                    else -> error("can't compare $av")
                }.contains(asString(value.eval(item, values)).toString())
        }

        data class And(val left: Expr, val right: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues): Boolean =
                (left.eval(item, values) as Boolean) && (right.eval(item, values) as Boolean)
        }

        data class Or(val left: Expr, val right: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                (left.eval(item, values) as Boolean) || (right.eval(item, values) as Boolean)
        }

        data class Not(val expr: Expr) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                !(expr.eval(item, values) as Boolean)
        }

        data class ConditionAttributeValue(val value: String) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                item[AttributeName.of(value)] ?: Null()
        }

        data class ExpressionAttributeValue(val value: String) : Expr() {
            override fun eval(item: Item, values: TokensToValues) =
                values[value] ?: error("missing value $value")
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
}
