package org.http4k.connect.amazon.ses.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

sealed class Body(value: String) : StringValue(value)

class TextMessage(value: String) : Body(value) {
    companion object : NonBlankStringValueFactory<TextMessage>(::TextMessage)
}

class HtmlMessage(value: String) : Body(value) {
    companion object : NonBlankStringValueFactory<HtmlMessage>(::HtmlMessage)
}
