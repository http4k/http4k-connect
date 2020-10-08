package org.http4k.connect

data class Pages(val pageNumber: Int, val maxPages: Int?)

sealed class Listing<out T> : Iterable<T> {
    object Empty : Listing<Nothing>(), Iterable<Nothing> by emptyList()

    data class Unpaged<T>(val items: List<T>) : Listing<T>(), Iterable<T> by items

    data class Paged<T>(val items: List<T>, val page: Pages) : Listing<T>(), Iterable<T> by items
}

