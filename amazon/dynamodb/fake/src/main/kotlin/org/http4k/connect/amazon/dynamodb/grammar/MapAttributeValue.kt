package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import parser4k.Parser
import parser4k.commonparsers.token
import parser4k.inOrder
import parser4k.map
import parser4k.ref

fun MapAttributeValue(parser: () -> Parser<Expr>) = inOrder(ref(parser), token("."), ref(parser))
    .map { (parent, _, child) ->
        Expr {
            item ->
            child.eval(item.copy(item = (parent.eval(item) as AttributeValue).M ?: emptyMap()))
        }
    }
