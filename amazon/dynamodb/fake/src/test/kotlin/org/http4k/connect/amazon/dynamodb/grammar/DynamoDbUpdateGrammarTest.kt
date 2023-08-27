package org.http4k.connect.amazon.dynamodb.grammar


import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.dynamodb.attrBool
import org.http4k.connect.amazon.dynamodb.attrN
import org.http4k.connect.amazon.dynamodb.attrS
import org.http4k.connect.amazon.dynamodb.attrSS
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues
import org.junit.jupiter.api.Test

class DynamoDbUpdateGrammarTest {

    @Test
    fun `remove - single action`() {
        val item = Item(attrS of "a", attrN of 1)

        assert("REMOVE $attrN", item, Item(attrS of "a"))
    }

    @Test
    fun `remove - named`() {
        val item = Item(attrS of "a", attrN of 1)

        assert("REMOVE #key1", item, Item(attrS of "a"), names = mapOf("#key1" to attrN.name))
    }

    @Test
    fun `remove - two actions`() {
        val item = Item(attrS of "a", attrN of 1, attrSS of setOf("b"))

        assert("REMOVE $attrN $attrSS", item, Item(attrS of "a"))
    }

    @Test
    fun `set - missing attribute`() {
        val item = Item(attrS of "a")

        assert(
            expression = "SET $attrN = :val1",
            item = item,
            expected = Item(attrS of "a", attrN of 1),
            values = mapOf(":val1" to attrN.asValue(1))
        )
    }

    @Test
    fun `set - existing attribute`() {
        val item = Item(attrS of "a", attrN of 1)

        assert(
            expression = "SET $attrN = :val1",
            item = item,
            expected = Item(attrS of "a", attrN of 2),
            values = mapOf(":val1" to attrN.asValue(2))
        )
    }

    @Test
    fun `set - to self, named`() {
        val item = Item(attrS of "a", attrN of 1)

        assert(
            expression = "SET #key1 = #key1",
            item = item,
            expected = Item(attrS of "a", attrN of 1),
            names = mapOf("#key1" to attrN.name),
            values = mapOf(":val1" to attrN.asValue(1))
        )
    }

    @Test
    fun `set - plus value`() {
        val item = Item(attrS of "a", attrN of 1)

        assert(
            expression = "SET $attrN = $attrN + :val1",
            item = item,
            expected = Item(attrS of "a", attrN of 2),
            values = mapOf(":val1" to attrN.asValue(1))
        )
    }

    @Test
    fun `set - plus item value`() {
        val item = Item(attrS of "a", attrN of 4)

        assert(
            expression = "SET $attrN = $attrN + #key1",
            item = item,
            expected = Item(attrS of "a", attrN of 8),
            names = mapOf("#key1" to attrN.name)
        )
    }

    @Test
    fun `set - minus value`() {
        val item = Item(attrS of "a", attrN of 2)

        assert(
            expression = "SET $attrN = $attrN - :val1",
            item = item,
            expected = Item(attrS of "a", attrN of 1),
            values = mapOf(":val1" to attrN.asValue(1))
        )
    }

    @Test
    fun `set - minus item value`() {
        val item = Item(attrS of "a", attrN of 4)

        assert(
            expression = "SET $attrN = $attrN - #key1",
            item = item,
            expected = Item(attrS of "a", attrN of 0),
            names = mapOf("#key1" to attrN.name)
        )
    }

    @Test
    fun `set - multiple`() {
        val item = Item(attrS of "a", attrN of 4)

        assert(
            expression = "SET $attrN = :val1, $attrBool = :val2",
            item = item,
            expected = Item(attrS of "a", attrN of 2, attrBool of true),
            values = mapOf(":val1" to attrN.asValue(2), ":val2" to attrBool.asValue(true))
        )
    }

    @Test
    fun `multiple operations`() {
        val item = Item(attrS of "a", attrN of 1)

        assert(
            expression = "SET #key1 = :val1 REMOVE #key2",
            item = item,
            expected = Item(attrS of "a", attrBool of true),
            names = mapOf("#key1" to attrBool.name, "#key2" to attrN.name),
            values = mapOf(":val1" to attrBool.asValue(true))
        )
    }
}

private fun assert(
    expression: String,
    item: Item,
    expected: Item,
    values: TokensToValues = emptyMap(),
    names: TokensToNames = emptyMap()
) {
    assert(expression, ItemWithSubstitutions(item, names, values), expected)
}

private fun assert(
    expression: String,
    item: ItemWithSubstitutions,
    expected: Item
) {
    val dynamoDbGrammar = DynamoDbUpdateGrammar.parse(expression)
    assertThat(
        "\nexpression=$expression\nitem=${item.item}\nvalues=${item.values}\nnames=${item.names}\n",
        dynamoDbGrammar.eval(item), equalTo(expected)
    )
}
