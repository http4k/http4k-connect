package org.http4k.connect.amazon.dynamodb.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class KeySchema(
    val AttributeName: AttributeName,
    val KeyType: KeyType
)
