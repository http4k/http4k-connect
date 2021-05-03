package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.AttributeName
import parser4k.Parser
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.token
import parser4k.inOrder
import parser4k.map
import parser4k.skipWrapper

fun AttributeNotExists(parser: () -> Parser<Expr>) =
    inOrder(token("attribute_not_exists"), token("("), Tokens.identifier, token(")"))
        .skipWrapper()
        .map { (_, name) ->
            Expr {
                !it.item.containsKey(AttributeName.of(name))
            }
        }
