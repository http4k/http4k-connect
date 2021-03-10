package org.http4k.connect.amazon.model

import dev.forkhandles.values.LongValue
import dev.forkhandles.values.LongValueFactory
import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.Value
import dev.forkhandles.values.and
import dev.forkhandles.values.minLength
import dev.forkhandles.values.minValue
import dev.forkhandles.values.regex
import org.http4k.base64Decoded
import org.http4k.core.Uri
import se.ansman.kotshi.JsonSerializable
import java.io.InputStream
import java.util.Base64

class AccessKeyId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<AccessKeyId>(::AccessKeyId)
}

class ARN private constructor(value: String) : StringValue(value) {
    private val parts = value.split(":", "/")
    val partition = parts[1]
    val awsService = AwsService.of(parts[2])
    val region by lazy { Region.of(parts[3]) }
    val account by lazy { AwsAccount.of(parts[4]) }

    private val resource = parts.drop(5)

    val resourceType by lazy {
        if (resource.size == 2) resource[0]
        else error("No resource type found in $value")
    }

    fun <T : ResourceId> resourceId(fn: (String) -> T) =
        if (resource.size == 2) fn(resource[1])
        else fn(resource[0])

    companion object : StringValueFactory<ARN>(::ARN, 1.minLength.and { it.startsWith("arn:") }) {
        fun of(
            awsService: AwsService,
            region: Region,
            account: AwsAccount,
            resourceId: ResourceId,
            partition: String = "aws"
        ) = of("arn:$partition:$awsService:$region:$account:$resourceId")

        fun of(
            awsService: AwsService,
            region: Region,
            account: AwsAccount,
            resourcePath: String,
            partition: String = "aws"
        ) = of("arn:$partition:$awsService:$region:$account:$resourcePath")

        fun of(
            awsService: AwsService,
            region: Region,
            account: AwsAccount,
            resourceType: String,
            resourceId: ResourceId,
            partition: String = "aws"
        ) = of("arn:$partition:$awsService:$region:$account:$resourceType:$resourceId")
    }
}

fun StringValue.toARN() = ARN.of(value)

fun <T : Value<String>> StringValueFactory<T>.of(arn: ARN) = of(arn.value)

class AwsAccount private constructor(value: String) : StringValue(value.padStart(12, '0')) {
    companion object : StringValueFactory<AwsAccount>(::AwsAccount, 1.minLength.and { it.all(Char::isDigit) })
}

class AwsService private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<AwsService>(::AwsService)

    fun toUri(region: Region) = Uri.of("https://$this.${region}.amazonaws.com")
}

class Base64Blob private constructor(value: String) : StringValue(value) {
    fun decoded() = value.base64Decoded()
    fun decodedBytes() = value.base64Decoded().toByteArray()
    fun decodedInputStream() = value.base64Decoded().byteInputStream()

    companion object : NonBlankStringValueFactory<Base64Blob>(::Base64Blob) {
        fun encoded(unencoded: String) = encoded(unencoded.toByteArray())
        fun encoded(unencoded: ByteArray) = Base64Blob(Base64.getEncoder().encodeToString(unencoded))
        fun encoded(unencoded: InputStream) = encoded(unencoded.readAllBytes())
    }
}

class KMSKeyId private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<KMSKeyId>(::KMSKeyId) {
        fun of(arn: ARN) = of(arn.value)
    }
}

class Region private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<Region>(::Region, "[a-z]+-[a-z]+-\\d".regex)
}

abstract class ResourceId(value: String) : StringValue(value)

class SecretAccessKey private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<SecretAccessKey>(::SecretAccessKey)
}

class SessionToken private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<SessionToken>(::SessionToken)
}

@JsonSerializable
data class Tag(
    val Key: String,
    val Value: String
)

class Timestamp private constructor(value: Long) : LongValue(value) {
    companion object : LongValueFactory<Timestamp>(::Timestamp, 0L.minValue)
}
