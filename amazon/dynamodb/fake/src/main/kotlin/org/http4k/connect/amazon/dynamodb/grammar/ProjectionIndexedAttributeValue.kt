package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import parser4k.Parser
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.token
import parser4k.inOrder
import parser4k.map
import parser4k.ref

object ProjectionIndexedAttributeValue : ExprFactory {
    override operator fun invoke(parser: () -> Parser<Expr>): Parser<Expr> =
        inOrder(ref(parser), token("["), Tokens.number, token("]"))
            .map { (expr, _, index) ->
                Expr { item ->
                    (expr.eval(item) as List<AttributeNameValue>).map {
                        it.first to (it.second.L
                            ?.let { AttributeValue.List(listOf(it[index.toInt()])) }
                            ?: AttributeValue.Null())
                    }
                }
            }
}
