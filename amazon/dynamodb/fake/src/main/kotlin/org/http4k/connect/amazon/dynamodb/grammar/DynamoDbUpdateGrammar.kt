package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.Item
import parser4k.OutputCache
import parser4k.Parser
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.token
import parser4k.inOrder
import parser4k.map
import parser4k.oneOf
import parser4k.oneOfWithPrecedence
import parser4k.oneOrMore
import parser4k.parseWith
import parser4k.reset
import parser4k.skipFirst
import parser4k.with
import parser4k.optional

object DynamoDbUpdateGrammar {
    private val cache = OutputCache<Expr>()

    fun parse(expression: String): Expr = expression.parseWith(expr)

    private val expr = oneOrMore(
        oneOf(
            Set.with(cache),
            Remove.with(cache),
            // add
            // delete
        )
    ).map { updates ->
        Expr { item ->
            updates.fold(item) { curItem, expr ->
                curItem.copy(item = expr.eval(curItem) as Item)
            }.item
        }
    }.reset(cache)
}

private val UpdateAttributePath = oneOfWithPrecedence(
    inOrder(oneOf('#'), Tokens.identifier).skipFirst().map { ref ->
        Expr { item ->
            item.names["#$ref"] ?: error("missing name $ref")
        }
    },
    Tokens.identifier.map { name -> Expr { AttributeName.of(name) } }
)

private val UpdateExpressionPath = oneOf(
    // item value
    Tokens.identifier.map { name ->
        Expr { item ->
          item.item[AttributeName.of(name)] ?: error("missing item value $name")
        }
    },
    // named item value
    inOrder(oneOf('#'), Tokens.identifier).skipFirst().map { ref ->
        Expr { item ->
            val name = item.names["#$ref"] ?: error("missing name $ref")
            item.item[name] ?: error("missing item value $name")
        }
    },
    // named value
    inOrder(oneOf(':'), Tokens.identifier).skipFirst().map { name ->
        Expr { item ->
            item.values[":$name"] ?: error("missing value $name")
        }
    }
)

private val UpdateExpressionOperand = oneOf(
    UpdateExpressionPath,
    // function
)

private val UpdateExpressionValue = oneOfWithPrecedence(
    inOrder(
        UpdateExpressionOperand,
        token("+"),
        UpdateExpressionOperand
    ).map { (op1, _, op2) ->
        Expr { item ->
            (op1.eval(item) as AttributeValue) + (op2.eval(item) as AttributeValue)
        }
    },
    inOrder(
        UpdateExpressionOperand,
        token("-"),
        UpdateExpressionOperand
    ).map { (op1, _, op2) ->
        Expr { item ->
            val val1 = op1.eval(item) as AttributeValue
            val val2 = op2.eval(item) as AttributeValue
            val1 - val2
        }
    },
    UpdateExpressionOperand
)

private val Remove = inOrder(
    token("REMOVE"),
    oneOrMore(
        inOrder(
            Tokens.whitespace.optional(),
            UpdateAttributePath
        ).skipFirst()
    )
).skipFirst().map { names ->
    Expr { item ->
        val attributeNames = names.map { it.eval(item) }
        item.item.filterKeys { it !in attributeNames }
    }
}

private val Set = inOrder(
    token("SET"),
    oneOrMore(
        inOrder(
            token(",").optional(),
            inOrder(
                UpdateAttributePath,
                token("="),
                UpdateExpressionValue
            )
        ).skipFirst()
    )
).skipFirst().map { equations ->
    Expr { item ->
        item.item + equations.associate { (nameExp, _, valueExp) ->
            val name = nameExp.eval(item) as AttributeName
            val value = valueExp.eval(item) as AttributeValue
            name to value
        }
    }
}

// TODO contribute this to forkhandles
fun <T> Parser<T>.optional() = optional(this)
