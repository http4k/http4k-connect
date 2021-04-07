package org.http4k.connect.amazon.core.model

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class Base64BlobTest {

    @Test
    fun `encode decode string`() {
        val message = "hello"
        val encoded = Base64Blob.encode(message)
        assertThat(encoded.value, equalTo("aGVsbG8="))
        assertThat(encoded.decoded(), equalTo(message))
    }

    @Test
    fun `encode decode bytes`() {
        val message = "hello".toByteArray()
        val encoded = Base64Blob.encode(message)
        assertThat(encoded.value, equalTo("aGVsbG8="))
        assertThat(String(encoded.decodedBytes()), equalTo(String(message)))
    }

    @Test
    fun `encode decode stream`() {
        val message = "hello"
        val stream = message.byteInputStream()
        val encoded = Base64Blob.encode(stream)
        assertThat(encoded.value, equalTo("aGVsbG8="))
        assertThat(String(encoded.decodedInputStream().readAllBytes()), equalTo(message))
    }
}
