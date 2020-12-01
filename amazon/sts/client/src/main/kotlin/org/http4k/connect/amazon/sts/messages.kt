package org.http4k.connect.amazon.sts

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AssumeRoleResponse
import org.http4k.connect.amazon.model.Tag
import java.time.Duration

object AssumeRole {
    data class Request(
        val RoleArn: ARN,
        val RoleSessionName: String,
        val SerialNumber: Long? = null,
        val TokenCode: Int,
        val DurationSeconds: Duration? = null,
        val ExternalId: String? = null,
        val Policy: String? = null,
        val PolicyArns: List<ARN>? = null,
        val Tags: List<Tag>? = null,
        val TransitiveTagKeys: List<String>? = null
    )

    data class Response(val AssumeRoleResponse: AssumeRoleResponse)
}

