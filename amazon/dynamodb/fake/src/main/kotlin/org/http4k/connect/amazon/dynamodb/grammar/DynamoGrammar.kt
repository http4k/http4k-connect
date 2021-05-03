package org.http4k.connect.amazon.dynamodb.grammar

import dev.forkhandles.tuples.val2
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import parser4k.OutputCache
import parser4k.Parser
import parser4k.anyCharExcept
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
import parser4k.repeat
import parser4k.reset
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

    private val size = unaryExpr("size", ::Size)

    private val between = inOrder(ref { expr }, token("BETWEEN"), ref { expr }, token("AND"), ref { expr })
        .map {
            And(GreaterThanOrEqual(it.val1, it.val3), LessThanOrEqual(it.val1, it.val5))
        }.with(cache)

    private val `in` =
        inOrder(
            ref { expr },
            token("IN"),
            token("("),
            repeat(anyCharExcept(')')).map { it.joinToString("") },
            token(")")
        ).map {
            In(it.val1, it.val4.split(",")
                .map { it.trim().trimStart(':') }
                .map(::ExpressionAttributeValue))
        }.with(cache)

    private val attributeExists = inOrder(token("attribute_exists"), token("("), identifier, token(")"))
        .skipWrapper()
        .map { AttributeExists(AttributeName.of(it.val2)) }.with(cache)

    private val attributeNotExists = inOrder(token("attribute_not_exists"), token("("), identifier, token(")"))
        .skipWrapper()
        .map { AttributeNotExists(AttributeName.of(it.val2)) }.with(cache)

    private val paren = inOrder(token("("), ref { expr }, token(")")).skipWrapper().with(cache)
    private val not = unaryExpr("NOT", ::Not)
    private val and = binaryExpr("AND", ::And)
    private val or = binaryExpr("OR", ::Or)

    fun parse(expression: String): Expr = expression.parseWith(expr)

    fun parseProjection(expression: String): Expr {
        expression.split(",").map {
            it.trim().parseWith(expr)
        }

        return expression.parseWith(expr)
    }

    private val expr: Parser<Expr> = oneOfWithPrecedence(
        and,
        not,
        or,
        between,
        oneOf(
            equal,
            notEqual,
            lessThan,
            lessThanOrEqual,
            greaterThan,
            greaterThanOrEqual
        ),
        oneOf(
            `in`,
            size),
        oneOf(
            attributeExists,
            attributeNotExists,
            AttributeType.parser(::expr).with(cache),
            BeginsWith.parser(::expr).with(cache),
            Contains.parser(::expr).with(cache)
        ),
        paren.nestedPrecedence(),
        oneOfWithPrecedence(
            ExpressionAttributeName.parser(::expr).with(cache),
            MapAttributeValue.parser(::expr).with(cache)
        ),
        IndexedAttributeValue.parser(::expr).with(cache),
        ExpressionAttributeValue.parser(::expr).with(cache),
        ConditionAttributeValue.parser(::expr).with(cache)
    ).reset(cache)
}
