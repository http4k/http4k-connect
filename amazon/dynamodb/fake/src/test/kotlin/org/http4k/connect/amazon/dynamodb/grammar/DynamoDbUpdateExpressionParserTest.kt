package org.http4k.connect.amazon.dynamodb.grammar


import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.dynamodb.attrN
import org.http4k.connect.amazon.dynamodb.attrS
import org.http4k.connect.amazon.dynamodb.attrSS
import org.http4k.connect.amazon.dynamodb.model.Item
import org.junit.jupiter.api.Test

class DynamoDbUpdateExpressionParserTest {

    @Test
    fun `parse add action - two actions`() {
        assertThat(
            DynamoDbUpdateExpressionParser.parse("ADD $attrS :val1, #key1 :val2"),
            equalTo(listOf(
                AddAction(DocumentPath.Name(attrS.toString()), Operand.Token(":val1")),
                AddAction(DocumentPath.Token("#key1"), Operand.Token(":val2"))
            ))
        )
    }

    @Test
    fun `evaluate add action - add number`() {
        val action = AddAction(DocumentPath.Name(attrN.toString()), Operand.Token(":val1"))
        val values = mapOf(":val1" to attrN.asValue(2))
        val item = Item(attrN of 1)

        assertThat(
            action.eval(item, emptyMap(), values),
            equalTo(Item(attrN of 3))
        )
    }

    @Test
    fun `parse delete expression`() {
        assertThat(
            DynamoDbUpdateExpressionParser.parse("DELETE $attrSS :val1"),
            equalTo(listOf(
                DeleteAction(DocumentPath.Name(attrSS.toString()), Operand.Token(":val1"))
            ))
        )
    }

    @Test
    fun `evaluate delete expression`() {
        val action = DeleteAction(DocumentPath.Name(attrSS.toString()), Operand.Token(":val1"))
        val values = mapOf(":val1" to attrSS.asValue(setOf("baz")))
        val item = Item(attrSS of setOf("foo", "bar", "baz"))

        assertThat(
            action.eval(item, emptyMap(), values),
            equalTo(Item(attrSS of setOf("foo", "bar")))
        )
    }
}
