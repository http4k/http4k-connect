package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.Item
import parser4k.OutputCache
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
            Add.with(cache),
            Delete.with(cache)
        )
    ).map { updates ->
        Expr { item ->
            updates.fold(item) { curItem, expr ->
                curItem.copy(item = expr.eval(curItem) as Item)
            }.item
        }
    }.reset(cache)
}

// Components

private val Name = oneOfWithPrecedence(
    inOrder(oneOf('#'), Tokens.identifier).skipFirst().map { ref ->
        Expr { item ->
            item.names["#$ref"] ?: error("missing name $ref")
        }
    },
    Tokens.identifier.map { name -> Expr { AttributeName.of(name) } }
)

private val NamedValue = inOrder(oneOf(':'), Tokens.identifier).skipFirst().map { name ->
    Expr { item ->
        item.values[":$name"] ?: error("missing value $name")
    }
}

private val Value = oneOf(
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
    NamedValue
)

private val Operand = oneOf(
    Value,
    // function
)

private val ValueExpression = oneOfWithPrecedence(
    inOrder(
        Operand,
        token("+"),
        Operand
    ).map { (op1, _, op2) ->
        Expr { item ->
            (op1.eval(item) as AttributeValue) + (op2.eval(item) as AttributeValue)
        }
    },
    inOrder(
        Operand,
        token("-"),
        Operand
    ).map { (op1, _, op2) ->
        Expr { item ->
            val val1 = op1.eval(item) as AttributeValue
            val val2 = op2.eval(item) as AttributeValue
            val1 - val2
        }
    },
    Operand
)

private val NameValuePair = inOrder(
    optional(token(",")),
    Name,
    Tokens.whitespace,
    Value
).skipFirst()

// Actions

private val Remove = inOrder(
    token("REMOVE"),
    oneOrMore(
        inOrder(
            optional(Tokens.whitespace),
            Name
        ).skipFirst()
    )
).skipFirst().map { names ->
    Expr { item ->
        val attributeNames = names.map { it.eval(item) }
        item.item - attributeNames
    }
}

private val Set = inOrder(
    token("SET"),
    oneOrMore(
        inOrder(
            optional(token(",")),
            Name,
            token("="),
            ValueExpression
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

private val Add = inOrder(
    token("ADD"),
    oneOrMore(NameValuePair)
).skipFirst().map { adds ->
    Expr { item ->
        item.item + adds.map { (nameExp, _, valueExp) ->
            val name = nameExp.eval(item) as AttributeName
            val value = valueExp.eval(item) as AttributeValue
            name to (item.item[name]?.plus(value) ?: value)

        }
    }
}

private val Delete = inOrder(
    token("DELETE"),
    oneOrMore(NameValuePair)
).skipFirst().map { adds ->
    Expr { item ->
        item.item + adds.mapNotNull { (nameExp, _, valueExp) ->
            val name = nameExp.eval(item) as AttributeName
            item.item[name]?.let { existing ->
                val add = valueExp.eval(item) as AttributeValue
                name to (existing - add)
            }
        }
    }
}
