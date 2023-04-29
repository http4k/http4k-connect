package org.http4k.connect.amazon.dynamodb.model

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.values.InstantValue
import dev.forkhandles.values.InstantValueFactory
import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import org.junit.jupiter.api.Test
import java.time.Instant

class StringType private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<StringType>(::StringType)
}

class InstantType private constructor(value: Instant) : InstantValue(value) {
    companion object : InstantValueFactory<InstantType>(::InstantType)
}

class ExtensionsKtTest {

    @Test
    fun `non-string value list`() {
        val lens = Attribute.list(InstantType).required("foo")
        val input = listOf(InstantType.of(Instant.now()))
        val target = Item().with(lens of input)
        assertThat(lens(target), equalTo(input))
    }

    @Test
    fun `string value list`() {
        val lens = Attribute.list(StringType).required("foo")
        val input = listOf(StringType.of("foo"), StringType.of("bar"))
        val target = Item().with(lens of input)
        assertThat(lens(target), equalTo(input))
    }

    @Test
    fun `string value set`() {
        val lens = Attribute.strings(StringType).required("foo")
        val input = setOf(StringType.of("foo"), StringType.of("bar"))
        val target = Item().with(lens of input)
        assertThat(lens(target), equalTo(input))
    }
}
