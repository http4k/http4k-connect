package org.http4k.connect.amazon.dynamodb.grammar

import parser4k.Parser
import parser4k.ref

fun And(parser: () -> Parser<Expr>): Parser<Expr> =
    binaryExpr(ref(parser), "AND", ::And)

fun And(left: Expr, right: Expr) = Expr { item ->
    (left.eval(item) as Boolean) && (right.eval(item) as Boolean)
}
