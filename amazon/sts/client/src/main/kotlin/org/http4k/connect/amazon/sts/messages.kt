package org.http4k.connect.amazon.sts

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AssumeRoleResponse
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.TokenCode
import java.time.Duration

data class AssumeRole(
    val RoleArn: ARN,
    val RoleSessionName: String,
    val TokenCode: TokenCode? = null,
    val SerialNumber: Long? = null,
    val DurationSeconds: Duration? = null,
    val ExternalId: String? = null,
    val Policy: String? = null,
    val PolicyArns: List<ARN>? = null,
    val Tags: List<Tag>? = null,
    val TransitiveTagKeys: List<String>? = null
)

data class AssumedRole(val AssumeRoleResponse: AssumeRoleResponse)
