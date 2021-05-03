package org.http4k.connect.amazon.dynamodb.grammar

import parser4k.Parser
import parser4k.ref

fun Not(parser: () -> Parser<Expr>) =
    unaryExpr(ref(parser), "NOT") { expr ->
        Expr { !(expr.eval(it) as Boolean) }
    }
