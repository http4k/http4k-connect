package org.http4k.connect.amazon.dynamodb.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
enum class IndexStatus {
    CREATING, UPDATING, DELETING, ACTIVE
}
