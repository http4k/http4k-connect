package org.http4k.connect.amazon.core.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
enum class DataType {
    String, Number, Binary
}
