package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import parser4k.Parser
import parser4k.commonparsers.Tokens.number
import parser4k.commonparsers.token
import parser4k.inOrder
import parser4k.mapLeftAssoc
import parser4k.ref

object ProjectionIndexedAttributeValue : ExprFactory {
    override operator fun invoke(parser: () -> Parser<Expr>): Parser<Expr> =
        inOrder(ref(parser), token("["), number, token("]"))
            .mapLeftAssoc { (expr, _, index) ->
                Expr { item ->
                    println(expr)
                    println("INDEX! " + index)
                    val list = expr.eval(item) as List<AttributeNameValue> // we know this is true containing a list
                    val list2 = list.map {
                        it.first to AttributeValue.List(listOf(it.second.L!![index.toInt()]))
                    }
                    println(list2)
                    list2
//                    println("indexed! " + list[index.toInt()])
//                    val map = list.map { it.first to it.second.L!! }[index.toInt()]
//                    list[index.toInt()]
//                    println(list.map { it.second.L!! }[index.toInt()])
//                    val pair = list[index.toInt()]
//                        pair.first to (pair.second.L
//                            ?.let {
//                                AttributeValue.List(it)
//                            }
//                            ?: AttributeValue.Null())
                }
            }
}
