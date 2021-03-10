package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.ZonedDateTimeValue
import dev.forkhandles.values.ZonedDateTimeValueFactory
import dev.forkhandles.values.regex
import org.http4k.aws.AwsCredentials
import java.time.ZonedDateTime

class TokenCode private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<TokenCode>(::TokenCode, "[\\d]{6}".regex)
}

class RoleId private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<RoleId>(::RoleId)
}

class Expiration private constructor(value: ZonedDateTime) : ZonedDateTimeValue(value) {
    companion object : ZonedDateTimeValueFactory<Expiration>(::Expiration)
}

data class AssumedRoleUser(val Arn: ARN, val AssumedRoleId: RoleId)

data class Credentials(
    val SessionToken: SessionToken,
    val AccessKeyId: AccessKeyId,
    val SecretAccessKey: SecretAccessKey,
    val Expiration: Expiration
) {
    fun asHttp4k() = AwsCredentials(AccessKeyId.value, SecretAccessKey.value, SessionToken.value)
}
