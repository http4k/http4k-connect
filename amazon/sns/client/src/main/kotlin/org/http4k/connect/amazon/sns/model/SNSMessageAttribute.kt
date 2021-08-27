package org.http4k.connect.amazon.sns.model

import org.http4k.connect.amazon.core.model.DataType
import org.http4k.connect.amazon.core.model.MessageFields

sealed class SNSMessageAttribute(
    private val name: String,
    private val category: String,
    private val dataType: DataType? = null
) : MessageFields {
    override fun toFields(index: Int): Map<String, String> =
        (listOfNotNull(
            "$category.entry.$index.Name" to name,
            dataType?.let { "$category.entry.$index.Value.DataType" to it.name }
        ).toMap() + toCustomFields(index))

    protected abstract fun toCustomFields(index: Int): Map<String, String>

    internal class SingularValue(
        name: String,
        private val category: String,
        private val typePrefix: String,
        private val value: String,
        dataType: DataType?
    ) : SNSMessageAttribute(name, category, dataType) {
        override fun toCustomFields(index: Int) =
            mapOf("$category.entry.$index.Value.${typePrefix}Value" to value)
    }

    internal class ListValue(
        private val values: List<String>,
        private val category: String,
        private val typePrefix: String,
        name: String,
        dataType: DataType?
    ) : SNSMessageAttribute(name, category, dataType) {
        override fun toCustomFields(index: Int) =
            values.mapIndexed { i, it ->
                "$category.entry.$index.Value.${typePrefix}Value.${i + 1}" to it
            }.toMap()
    }
}
