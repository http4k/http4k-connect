package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.regex

class AttributeName private constructor(value: String) : StringValue(value), Comparable<AttributeName> {
    companion object : NonBlankStringValueFactory<AttributeName>(::AttributeName)

    override fun compareTo(other: AttributeName): Int = value.compareTo(other.value)
}


class IndexName private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<IndexName>(::IndexName)
}

class TableName private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<TableName>(::TableName, "[a-zA-Z0-9_.-]+".regex)
}
