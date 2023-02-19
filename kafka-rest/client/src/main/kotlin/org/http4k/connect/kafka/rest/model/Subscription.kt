package org.http4k.connect.kafka.rest.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Subscription(val topics: List<Topic>)
