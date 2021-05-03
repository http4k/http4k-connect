package org.http4k.connect.amazon.dynamodb.grammar

import parser4k.OutputCache
import parser4k.Parser
import parser4k.nestedPrecedence
import parser4k.oneOf
import parser4k.oneOfWithPrecedence
import parser4k.parseWith
import parser4k.reset
import parser4k.with

object DynamoDbGrammar {
    private val cache = OutputCache<Expr>()

    fun parse(expression: String): Expr = expression.parseWith(expr)

    fun parseProjection(expression: String): Expr {
        expression.split(",").map {
            it.trim().parseWith(expr)
        }

        return expression.parseWith(expr)
    }

    private val expr: Parser<Expr> = oneOfWithPrecedence(
        And(::expr).with(cache),
        Not(::expr).with(cache),
        Or(::expr).with(cache),
        Between(::expr).with(cache),
        oneOfWithCache(Equal, NotEqual, LessThan, LessThanOrEqual, GreaterThan, GreaterThanOrEqual),
        oneOfWithCache(In, Size),
        oneOfWithCache(AttributeExists, AttributeNotExists, AttributeType, BeginsWith, Contains),
        Paren(::expr).with(cache).nestedPrecedence(),
        oneOfWithPrecedenceWithCache(ExpressionAttributeName, MapAttributeValue),
        IndexedAttributeValue(::expr).with(cache),
        ExpressionAttributeValue(::expr).with(cache),
        ConditionAttributeValue(::expr).with(cache)
    ).reset(cache)

    private fun oneOfWithCache(vararg fn: ExprFactory): Parser<Expr> = oneOf(fn.map { it(::expr).with(cache) })
    private fun oneOfWithPrecedenceWithCache(vararg fn: ExprFactory): Parser<Expr> =
        oneOfWithPrecedence(fn.map { it(::expr).with(cache) })
}
