package org.http4k.connect

data class Pages(val pageNumber: Int, val maxPages: Int?)

/**
 * Common type representing a list of results coming from a service.
 */
sealed class Listing<out T> : Iterable<T> {
    object Empty : Listing<Nothing>(), Iterable<Nothing> by emptyList()

    data class Unpaged<T>(val items: List<T>) : Listing<T>(), Iterable<T> by items

    data class Paged<T>(val items: List<T>, val page: Pages) : Listing<T>(), Iterable<T> by items

    data class Tokenized<T, TOKEN>(val items: List<T>, val nextToken: TOKEN) : Listing<T>(), Iterable<T> by items
}

