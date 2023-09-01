package org.http4k.connect.amazon.evidently.model

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import org.http4k.connect.amazon.evidently.EvidentlyMoshi

class EvaluationContext private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<EvaluationContext>(::EvaluationContext) {
        fun of(json: Map<String, Any>) = of(EvidentlyMoshi.asFormatString(json)) // FIXME does this work without reflection?
    }
}
