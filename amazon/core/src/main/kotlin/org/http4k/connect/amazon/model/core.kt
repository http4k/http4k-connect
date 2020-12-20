package org.http4k.connect.amazon.model

import dev.forkhandles.values.LongValue
import dev.forkhandles.values.LongValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.and
import dev.forkhandles.values.minLength
import dev.forkhandles.values.minValue
import dev.forkhandles.values.regex
import org.http4k.base64Decoded
import org.http4k.base64Encode
import se.ansman.kotshi.JsonSerializable

class ARN private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<ARN>(::ARN, 1.minLength.and { it.startsWith("arn:aws:") }) {
        fun of(region: Region,
               awsService: AwsService,
               resourceType: String,
               resourceId: String,
               account: AwsAccount) = of(
            "arn:aws:$awsService:$region:$account:$resourceType:$resourceId")
    }
}

fun StringValue.toARN() = ARN.of(value)

fun <T> StringValueFactory<T>.of(arn: ARN) = of(arn.value)

class AwsAccount private constructor(value: String) : StringValue(value.padStart(12, '0')) {
    companion object : StringValueFactory<AwsAccount>(::AwsAccount, 1.minLength.and { it.all(Char::isDigit) })
}

class AwsService private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<AwsService>(::AwsService, 1.minLength)
}

class Timestamp private constructor(value: Long) : LongValue(value) {
    companion object : LongValueFactory<Timestamp>(::Timestamp, 0L.minValue)
}

class Base64Blob private constructor(value: String) : StringValue(value) {
    fun decoded() = value.base64Decoded()

    companion object : StringValueFactory<Base64Blob>(::Base64Blob, 1.minLength) {
        fun encoded(unencoded: String) = Base64Blob(unencoded.base64Encode())
    }
}

class Region private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<Region>(::Region, "[a-z]+-[a-z]+-\\d".regex)
}

class KMSKeyId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<KMSKeyId>(::KMSKeyId, 1.minLength) {
        fun of(arn: ARN) = of(arn.value)
    }
}

@JsonSerializable
data class Tag(
    val Key: String,
    val Value: String
)
