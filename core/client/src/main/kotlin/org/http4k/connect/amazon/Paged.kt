package org.http4k.connect.amazon

/**
 * Represents a Paged response
 */
interface Paged<Token, ItemType> {
    fun token(): Token?
    val items: List<ItemType>
}
