package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import parser4k.Parser
import parser4k.commonparsers.Tokens.identifier
import parser4k.map

object ConditionAttributeValue : ExprFactory {
    override operator fun invoke(parser: () -> Parser<Expr>): Parser<Expr> = identifier.map(::ConditionAttributeValue)
}

fun ConditionAttributeValue(value: String) = Expr { item ->
    item.item[AttributeName.of(value)] ?: AttributeValue.Null()
}
