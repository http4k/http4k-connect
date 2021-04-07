package org.http4k.connect.amazon.sqs.model

import org.http4k.connect.amazon.model.DataType
import org.http4k.connect.amazon.model.MessageFields

sealed class SQSMessageAttribute(
    private val name: String,
    private val category: String,
    private val dataType: DataType? = null) : MessageFields {
    override fun toFields(index: Int): Map<String, String> =
        (listOfNotNull(
            "$category.$index.Name" to name,
            dataType?.let { "$category.$index.Value.DataType" to it.name }
        ).toMap() + toCustomFields(index))

    protected abstract fun toCustomFields(index: Int): Map<String, String>

    internal class SingularValue(
        name: String,
        private val category: String,
        private val typePrefix: String,
        private val value: String,
        dataType: DataType?
    ) : SQSMessageAttribute(name, category, dataType) {
        override fun toCustomFields(index: Int) =
            mapOf("$category.$index.Value.${typePrefix}Value" to value)
    }

    internal class ListValue(
        private val values: List<String>,
        private val category: String,
        private val typePrefix: String,
        name: String,
        dataType: DataType?
    ) : SQSMessageAttribute(name, category, dataType) {
        override fun toCustomFields(index: Int) =
            values.mapIndexed { i, it ->
                "$category.$index.Value.${typePrefix}Value.${i + 1}" to it
            }.toMap()
    }
}
