package org.http4k.connect.amazon.core.model

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import junit.framework.Assert.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.random.Random

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
    fun `encode decode array`() {
        val message = Random(0).nextBytes(50)
        val encoded = Base64Blob.encode(message)
        assertThat(encoded.value, equalTo("LMK0jFCu/lOzl07ZHmtOqST5uqjne8wvU38LAu/oYDCsLDFTRqj13MCnlgL5pRMQ2Yg="))
        assertTrue(encoded.decodedBytes().contentEquals(message))
    }

    @Test
    fun `encode decode stream`() {
        val message = Random(0).nextBytes(50)
        val stream = ByteArrayInputStream(message)
        val encoded = Base64Blob.encode(stream)
        assertThat(encoded.value, equalTo("LMK0jFCu/lOzl07ZHmtOqST5uqjne8wvU38LAu/oYDCsLDFTRqj13MCnlgL5pRMQ2Yg="))
        assertTrue(encoded.decodedInputStream().readAllBytes().contentEquals(message))
    }
}
