package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.Value
import dev.forkhandles.values.ZonedDateTimeValueFactory
import dev.forkhandles.values.regex
import org.xml.sax.InputSource
import java.io.StringReader
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

class SessionToken private constructor(value: String) : Value<String>(value) {
    companion object : StringValueFactory<SessionToken>(::SessionToken)
}

class AccessKeyId private constructor(value: String) : Value<String>(value) {
    companion object : StringValueFactory<AccessKeyId>(::AccessKeyId)
}

class TokenCode private constructor(value: String) : Value<String>(value) {
    companion object : StringValueFactory<TokenCode>(::TokenCode, "[\\d]{6}".regex)
}

class RoleId private constructor(value: String) : Value<String>(value) {
    companion object : StringValueFactory<RoleId>(::RoleId)
}

class SecretAccessKey private constructor(value: String) : Value<String>(value) {
    companion object : StringValueFactory<SecretAccessKey>(::SecretAccessKey)
}

class Expiration private constructor(value: ZonedDateTime) : Value<ZonedDateTime>(value) {
    companion object : ZonedDateTimeValueFactory<Expiration>(::Expiration, formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"))
}

data class AssumedRoleUser(
    val Arn: ARN,
    val AssumedRoleId: RoleId
)

data class Credentials(
    val SessionToken: SessionToken,
    val AccessKeyId: AccessKeyId,
    val SecretAccessKey: SecretAccessKey,
    val Expiration: Expiration
)

data class AssumeRoleResult(val PackedPolicySize: Int,
                            val AssumedRoleUser: AssumedRoleUser,
                            val Credentials: Credentials
)

data class ResponseMetadata(val RequestId: String)

data class AssumeRoleResponse(val AssumeRoleResult: AssumeRoleResult, val ResponseMetadata: ResponseMetadata)

internal val documentBuilderFactory by lazy {
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
}
