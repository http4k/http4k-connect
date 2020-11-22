package org.http4k.connect.amazon.model

import dev.forkhandles.values.LongValue
import dev.forkhandles.values.NonEmptyStringValue
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.exactLength
import dev.forkhandles.values.regex
import org.http4k.base64Decoded
import org.http4k.base64Encode

class ARN(value: String) : NonEmptyStringValue(value) {
    constructor(region: Region,
                awsService: AwsService,
                resourceType: String,
                resourceId: String,
                account: AwsAccount) : this(
        "arn:aws:$awsService:$region:$account:$resourceType:$resourceId"
    )
}

class AwsAccount(value: Long) : StringValue(value.toString().padStart(12, '0'), 12.exactLength)

class AwsService(value: String) : NonEmptyStringValue(value)

class Timestamp(value: Long) : LongValue(value)

class Base64Blob constructor(value: String) : NonEmptyStringValue(value) {
    val base64Encoded: String get() = value

    fun decoded() = value.base64Decoded().toByteArray()

    companion object {
        fun encoded(unencoded: String) = Base64Blob(unencoded.base64Encode())
    }
}

class Region(value: String) : StringValue(value, "[a-z]+-[a-z]+-\\d".regex)
