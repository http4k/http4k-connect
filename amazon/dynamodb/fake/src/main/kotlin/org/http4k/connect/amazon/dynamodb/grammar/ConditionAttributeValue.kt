package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import parser4k.Parser
import parser4k.commonparsers.Tokens
import parser4k.map

fun ConditionAttributeValue(parser: () -> Parser<Expr>): Parser<Expr> = Tokens.identifier
    .map { value ->
        ConditionAttributeValue(value)
    }

fun ConditionAttributeValue(value: String) = Expr { item ->
    item.item[AttributeName.of(value)] ?: AttributeValue.Null()
}
