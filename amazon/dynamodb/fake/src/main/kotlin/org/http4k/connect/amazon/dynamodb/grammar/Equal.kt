package org.http4k.connect.amazon.dynamodb.grammar

import parser4k.Parser
import parser4k.ref

fun Equal(parser: () -> Parser<Expr>): Parser<Expr> =
    binaryExpr(ref(parser), "=") { attr1, attr2 ->
        Expr {
            attr1.eval(it) == attr2.eval(it)
        }
    }
