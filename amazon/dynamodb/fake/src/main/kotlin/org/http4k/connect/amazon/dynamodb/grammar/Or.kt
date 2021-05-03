package org.http4k.connect.amazon.dynamodb.grammar

import parser4k.Parser
import parser4k.ref

fun Or(parser: () -> Parser<Expr>): Parser<Expr> =
    binaryExpr(ref(parser), "OR") { left, right ->
        Expr {
            (left.eval(it) as Boolean) || (right.eval(it) as Boolean)
        }
    }
