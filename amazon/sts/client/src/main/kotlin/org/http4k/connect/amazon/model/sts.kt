package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.ZonedDateTimeValue
import dev.forkhandles.values.ZonedDateTimeValueFactory
import dev.forkhandles.values.minLength
import dev.forkhandles.values.regex
import java.time.ZonedDateTime

class SessionToken private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<SessionToken>(::SessionToken, 1.minLength)
}

class AccessKeyId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<AccessKeyId>(::AccessKeyId, 1.minLength)
}

class TokenCode private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<TokenCode>(::TokenCode, "[\\d]{6}".regex)
}

class RoleId private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<RoleId>(::RoleId, 1.minLength)
}

class SecretAccessKey private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<SecretAccessKey>(::SecretAccessKey, 1.minLength)
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
)
