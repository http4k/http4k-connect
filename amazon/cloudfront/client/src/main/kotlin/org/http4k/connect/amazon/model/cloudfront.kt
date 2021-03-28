package org.http4k.connect.amazon.model

import dev.forkhandles.values.NonBlankStringValueFactory
import java.util.UUID

class CallerReference private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<CallerReference>(::CallerReference) {
        fun random() = of(UUID.randomUUID().toString())
    }
}

class DistributionId private constructor(value: String) : ResourceId(value) {
    companion object : NonBlankStringValueFactory<DistributionId>(::DistributionId)
}
