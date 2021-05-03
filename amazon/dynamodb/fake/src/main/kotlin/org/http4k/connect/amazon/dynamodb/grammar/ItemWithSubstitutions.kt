package org.http4k.connect.amazon.dynamodb.grammar

import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues

data class ItemWithSubstitutions(
    val item: Item,
    val values: TokensToValues = emptyMap(),
    val names: TokensToNames = emptyMap()
)
