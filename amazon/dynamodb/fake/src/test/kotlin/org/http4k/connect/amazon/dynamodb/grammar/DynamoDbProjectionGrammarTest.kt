package org.http4k.connect.amazon.dynamodb.grammar

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.Item
import org.junit.jupiter.api.Test

class DynamoDbProjectionGrammarTest {

    private val attrNum = Attribute.int().required("attrNum")
    private val attr1 = Attribute.string().required("attr1")
    private val attrList = Attribute.list().required("attrList")
    private val attrMap = Attribute.map().required("attrMap")
    private val attr2 = Attribute.duration().required("attr2")
    private val attr3 = Attribute.strings().required("attr3")

    @Test
    fun `attribute value`() {
        assertThat(
            DynamoDbProjectionGrammar.parse("attr1").eval(ItemWithSubstitutions(Item(attr1 of "123"))),
            equalTo(listOf(attr1.name to attr1.asValue("123")))
        )
    }

    @Test
    fun `indexed attribute value`() {
        assertThat(
            DynamoDbProjectionGrammar.parse("attrList[1]").eval(
                ItemWithSubstitutions(
                    Item(
                        attrList of listOf(
                            attr1.asValue("123"),
                            attrList.asValue(
                                listOf(attrNum.asValue(456))
                            )
                        )
                    )
                )
            ),
            equalTo(listOf(attrList.name to attrList.asValue(listOf(attrList.asValue(listOf(attrNum.asValue(456)))))))
        )
    }

    @Test
    fun `multiple indexed attribute value`() {
        assertThat(
            DynamoDbProjectionGrammar.parse("attrList[0][1]").eval(
                ItemWithSubstitutions(
                    Item(
                        attrList of listOf(
                            attrList.asValue( //0
                                listOf(
                                    attr1.asValue("123"), //0
                                    attrList.asValue( //1
                                        listOf(
                                            attr1.asValue("123"),
                                            attr1.asValue("123"),
                                            attrNum.asValue(456)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            equalTo(
                listOf(
                    attrList.name to attrList.asValue(
                        listOf(
                            attrList.asValue(
                                listOf(
                                    attrList.asValue(
                                        listOf(
                                            attr1.asValue("123"),
                                            attr1.asValue("123"),
                                            attrNum.asValue(456)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `map attribute value`() {
        assertThat(
            DynamoDbProjectionGrammar.parse("attrMap.attr1").eval(
                ItemWithSubstitutions(Item(attrMap of Item(attr1 of "456", attrNum of 456)))
            ),
            equalTo(listOf(attrMap.name to attrMap.asValue(Item(attr1 of "456"))))
        )
    }
}
