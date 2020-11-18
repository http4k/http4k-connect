package org.http4k.connect.amazon
import org.http4k.base64Decoded
import org.http4k.base64Encode

data class ARN(val value: String)

data class Timestamp(val value: Long)

data class Base64Blob constructor(val base64Encoded: String) {
    fun decoded() = base64Encoded.base64Decoded().toByteArray()

    companion object {
        fun encoded(unencoded: String) = Base64Blob(unencoded.base64Encode())
    }
}
