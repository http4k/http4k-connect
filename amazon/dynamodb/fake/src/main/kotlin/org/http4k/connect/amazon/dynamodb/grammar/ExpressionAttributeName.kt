package org.http4k.connect.amazon.dynamodb.grammar

import parser4k.Parser
import parser4k.commonparsers.Tokens
import parser4k.inOrder
import parser4k.map
import parser4k.oneOf
import parser4k.skipFirst

fun ExpressionAttributeName(parser: () -> Parser<Expr>): Parser<Expr> = inOrder(oneOf('#'), Tokens.identifier)
    .skipFirst().map { value ->
        Expr { item ->
            (item.names[value] ?: error("missing name $value")).let {
                ConditionAttributeValue(it.value).eval(item)
            }
        }
    }
