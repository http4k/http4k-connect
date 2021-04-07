package org.http4k.connect.amazon.dynamodb.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.regex


class TableName private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<TableName>(::TableName, "[a-zA-Z0-9_.-]+".regex)
}
