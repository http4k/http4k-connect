package org.http4k.connect.amazon.evidently.model

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.and
import dev.forkhandles.values.maxLength
import dev.forkhandles.values.regex
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.ResourceId

class ProjectName private constructor(value: String) : ResourceId(value) {
    companion object : StringValueFactory<ProjectName>(::ProjectName, 2048.maxLength.and("^[-a-zA-Z0-9._]*\$".regex)) {
        fun of(arn: ARN) = arn.resourceId(::ProjectName)
    }
}
