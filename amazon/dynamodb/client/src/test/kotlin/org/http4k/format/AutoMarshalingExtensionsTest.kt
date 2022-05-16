package org.http4k.format

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.with
import org.http4k.core.Uri
import org.junit.jupiter.api.Test

data class AnObject(
    val str: String,
    val num: Int,
    val bool: Boolean,
    val uri: Uri,
    val list: List<ChildObject>,
    val map: ChildObject,
    val nullable: String?
)

data class ChildObject(val str: String)

class AutoMarshalingExtensionsTest {

    @Test
    fun `can roundtrip a dynamo item with autoDynamoLens`() {

        val input =
            AnObject("foobar", 123, false, Uri.of("http"), listOf(ChildObject("asd")), ChildObject("34534"), null)

        val lens = Moshi.autoDynamoLens<AnObject>()

        val item = Item().with(lens of input)

        assertThat(
            item, equalTo(
                mapOf(
                    AttributeName.of("str") to AttributeValue.Str(input.str),
                    AttributeName.of("num") to AttributeValue.Num(input.num.toDouble()),
                    AttributeName.of("bool") to AttributeValue.Bool(input.bool),
                    AttributeName.of("uri") to AttributeValue.Str(input.uri.toString()),
                    AttributeName.of("list") to AttributeValue.List(
                        listOf(
                            AttributeValue.Map(
                                mapOf(AttributeName.of("str") to AttributeValue.Str(input.list[0].str))
                            )
                        )
                    ),
                    AttributeName.of("map") to AttributeValue.Map(
                        mapOf(AttributeName.of("str") to AttributeValue.Str(input.map.str))
                    )
                )
            )
        )

        assertThat(lens(item), equalTo(input))
    }
}
