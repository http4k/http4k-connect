package org.http4k.connect.amazon.dynamodb

import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableDescription

data class DynamoTable(val table: TableDescription, val items: List<Item> = emptyList()) {
    fun retrieve(key: Key) = items.firstOrNull { it.matches(key) }

    fun withItem(item: Item) = copy(items = items + item)
    fun withoutItem(key: Key) = copy(items = items.filterNot { it.matches(key) })

    private fun Item.matches(key: Key) = toList().containsAll(key.toList())
}
