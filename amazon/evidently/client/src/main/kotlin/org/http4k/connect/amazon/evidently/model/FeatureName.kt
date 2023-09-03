package org.http4k.connect.amazon.evidently.model

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.length
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.ResourceId

class FeatureName private constructor(value: String) : ResourceId(value) {
    companion object : StringValueFactory<FeatureName>(::FeatureName, (1..127).length) {
        fun of(arn: ARN) = arn.resourceId(::FeatureName)
    }
}
