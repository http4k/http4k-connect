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
    fun `parse set expression with two actions`() {
        assertThat(
            DynamoDbUpdateExpressionParser.parse("SET ProductCategory = :c, #key2 = :p"),
            equalTo(listOf(
                SetAction(
                    path = DocumentPath.Name("ProductCategory"),
                    function = Operand.Token(":c")
                ),
                SetAction(
                    path = DocumentPath.Token("#key2"),
                    function = Operand.Token(":p")
                )
            ))
        )
    }

    @Test
    fun `evaluate set action - of value`() {
        val action = SetAction(DocumentPath.Name(attrS.toString()), Operand.Token(":val1"))
        val values = mapOf(":val1" to attrS.asValue("bar"))

        val item = Item(attrS of "foo")

        assertThat(
            action.eval(item, emptyMap(), values),
            equalTo(Item(attrS of "bar"))
        )
    }

    @Test
    fun `parse remove expression with two actions`() {
        assertThat(
            DynamoDbUpdateExpressionParser.parse("REMOVE $attrS, $attrN"),
            equalTo(listOf(
                RemoveAction(DocumentPath.Name(attrS.toString())),
                RemoveAction(DocumentPath.Name(attrN.toString()))
            ))
        )
    }

    @Test
    fun `evaluate remove action - of name`() {
        val action = RemoveAction(DocumentPath.Name(attrS.toString()))
        val item = Item(attrS of "foo", attrN of 123)

        assertThat(
            action.eval(item, emptyMap(), emptyMap()),
            equalTo(Item(attrN of 123))
        )
    }

    @Test
    fun `evaluate remove action - of token`() {
        val action = RemoveAction(DocumentPath.Token("#key1"))
        val item = Item(attrS of "foo", attrN of 123)
        val names = mapOf("#key1" to attrN.name)

        assertThat(
            action.eval(item, names, emptyMap()),
            equalTo(Item(attrS of "foo"))
        )
    }

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
