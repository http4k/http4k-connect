package org.http4k.connect.amazon.model

import dev.forkhandles.values.IntValue
import dev.forkhandles.values.IntValueFactory
import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

class Code private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<Code>(::Code)
}

class DeviceCode private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<DeviceCode>(::DeviceCode)
}

class Scope private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<Scope>(::Scope)
}

class ExpiresIn private constructor(value: Int) : IntValue(value) {
    companion object : IntValueFactory<ExpiresIn>(::ExpiresIn)
}

class Interval private constructor(value: Int) : IntValue(value) {
    companion object : IntValueFactory<Interval>(::Interval)
}

class SessionId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<SessionId>(::SessionId)
}

class TokenType private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<TokenType>(::TokenType)
}
