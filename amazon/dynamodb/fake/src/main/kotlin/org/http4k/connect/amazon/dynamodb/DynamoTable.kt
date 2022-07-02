package org.http4k.connect.amazon.dynamodb

import org.http4k.connect.amazon.dynamodb.endpoints.keySchema
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableDescription

data class DynamoTable(val table: TableDescription, val items: List<Item> = emptyList()) {
    fun retrieve(key: Key) = items.firstOrNull { it.matches(key) }

    fun withItem(item: Item) = retrieve(item.key())
        .let { existing -> if (existing != null) withoutItem(existing) else this }
        .let { it.copy(items = it.items + item) }

    fun withoutItem(key: Key) = copy(items = items.filterNot { it.matches(key) })

    private fun Item.matches(key: Key) = toList().containsAll(key.toList())
    private fun Item.key(): Key {
        val schema = keySchema() ?: return Key()

        return schema.mapNotNull { key ->
            val value = this[key.AttributeName]
            if (value == null) null else key.AttributeName to value
        }.toMap()
    }
}
