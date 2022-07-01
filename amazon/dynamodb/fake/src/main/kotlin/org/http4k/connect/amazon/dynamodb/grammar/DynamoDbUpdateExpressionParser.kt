package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues

object DynamoDbUpdateExpressionParser {

    fun parse(expression: String): List<UpdateAction> = when {
        expression.startsWith("SET", ignoreCase = true) -> expression
            .replace("SET", "", ignoreCase = true)
            .split(",")
            .map { SetAction.parse(it) }

        expression.startsWith("REMOVE", ignoreCase = true) -> expression
            .replace("REMOVE", "", ignoreCase = true)
            .split(",")
            .map { RemoveAction.parse(it) }

        expression.startsWith("ADD", ignoreCase = true) -> expression
            .replace("ADD", "", ignoreCase = true)
            .split(",")
            .map { AddAction.parse(it) }

        expression.startsWith("DELETE", ignoreCase = true) -> expression
            .replace("DELETE", "", ignoreCase = true)
            .split(",")
            .map { DeleteAction.parse(it) }

        else -> throw IllegalArgumentException("Could not parse expression: $expression")
    }
}

sealed interface UpdateAction {
    fun eval(item: Item, names: TokensToNames, values: TokensToValues): Item
}

data class SetAction(val path: DocumentPath, val function: UpdateValue): UpdateAction {
    override fun eval(item: Item, names: TokensToNames, values: TokensToValues): Item {
        val name = path.eval(names)
        val value = function.eval(item, values)
        return item + (name to value)
    }

    companion object {
        fun parse(expression: String): SetAction {
            val (path, value) = expression.trim().split("=")
                .map { it.trim() }

            val name = DocumentPath.parse(path)
            val function = UpdateValue.parse(value)
            return SetAction(name, function)
        }
    }
}

// TODO support removing elements from list
data class RemoveAction(val path: DocumentPath): UpdateAction {
    override fun eval(item: Item, names: TokensToNames, values: TokensToValues): Item {
        val name = path.eval(names)
        return item.minus(name)
    }

    companion object {
        fun parse(expression: String): RemoveAction {
            val name = DocumentPath.parse(expression)
            return RemoveAction(name)
        }
    }
}

data class AddAction(val path: DocumentPath, val operand: Operand): UpdateAction {
    override fun eval(item: Item, names: TokensToNames, values: TokensToValues): Item {
        val name = path.eval(names)
        val existing = item[name] ?: return item
        val value = operand.eval(item, values)

        val updatedValue = existing + value

        return item + (name to updatedValue)
    }

    companion object {
        fun parse(expression: String): AddAction {
            val (path, value) = expression.trim().split(" ")
                .map { it.trim() }

            val name = DocumentPath.parse(path)
            val operand = Operand.parse(value)

            return AddAction(name, operand)
        }
    }
}
data class DeleteAction(val path: DocumentPath, val operand: Operand): UpdateAction {
    override fun eval(item: Item, names: TokensToNames, values: TokensToValues): Item {
        val name = path.eval(names)
        val existing = item[name] ?: return item
        val value = operand.eval(item, values)

        val updatedValue = existing - value

        return item + (name to updatedValue)
    }

    companion object {
        fun parse(expression: String): DeleteAction {
            val (pathExp, valueExp) = expression.trim().split(" ")
                .map { it.trim() }

            val path = DocumentPath.parse(pathExp)
            val value = Operand.parse(valueExp)

            return DeleteAction(path, value)
        }
    }
}

sealed interface DocumentPath {
    fun eval(names: TokensToNames): AttributeName

    data class Name(val name: String): DocumentPath {
        override fun eval(names: TokensToNames): AttributeName {
            return AttributeName.of(name)
        }
    }

    data class Token(val token: String): DocumentPath {
        override fun eval(names: TokensToNames): AttributeName {
           return names[token] ?: throw java.lang.IllegalArgumentException("Name not found: $token")
        }
    }

    companion object {
        fun parse(expression: String): DocumentPath {
            return if (expression.trim().startsWith("#")) {
                Token(expression.trim())
            } else {
                Name(expression.trim())
            }
        }
    }
}

sealed interface UpdateValue {
    fun eval(item: Item, values: TokensToValues): AttributeValue

    companion object {
        fun parse(expression: String): UpdateValue = when {
            "+" in expression -> Plus.parse(expression)
            "-" in expression -> Minus.parse(expression)
            else -> Operand.parse(expression)
        }
    }

    data class Plus(val op1: Operand, val op2: Operand): UpdateValue {
        override fun eval(item: Item, values: TokensToValues): AttributeValue {
            return op1.eval(item, values) + op2.eval(item, values)
        }

        companion object {
            fun parse(expression: String): Plus {
                val (op1, op2) = expression.split("+").map { it.trim() }
                return Plus(Operand.parse(op1), Operand.parse(op2))
            }
        }
    }

    data class Minus(val op1: Operand, val op2: Operand): UpdateValue {
        override fun eval(item: Item, values: TokensToValues): AttributeValue {
            return op1.eval(item, values) - op2.eval(item, values)
        }

        companion object {
            fun parse(expression: String): Minus {
                val (op1, op2) = expression.split("-").map { it.trim() }
                return Minus(Operand.parse(op1), Operand.parse(op2))
            }
        }
    }
}

sealed interface Operand: UpdateValue {

    data class Path(val path: AttributeName): Operand {
        override fun eval(item: Item, values: TokensToValues): AttributeValue {
            return item[path] ?: throw IllegalArgumentException("Attribute $path not found in $item")
        }
    }

    data class Token(val token: String): Operand {
        override fun eval(item: Item, values: TokensToValues): AttributeValue {
            return values[token] ?: throw IllegalArgumentException("Token $token not found in $values")
        }
    }

    companion object {
        fun parse(value: String): Operand {
            return if (value.startsWith(":")) {
                Token(value)
            } else {
                Path(AttributeName.of(value))
            }
        }
    }
}
