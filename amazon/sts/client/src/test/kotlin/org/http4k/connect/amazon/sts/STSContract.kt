package org.http4k.connect.amazon.sts

import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.filter.debug
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.ZonedDateTime
import java.util.UUID

abstract class STSContract(http: HttpHandler) : AwsContract(AwsService.of("sts"), http) {

    protected val clock = Clock.systemDefaultZone()
    private val sts by lazy {
        STS.Http(aws.scope, { aws.credentials }, http.debug(), clock)
    }

    @Test
    fun `assume role`() {
        val result = sts.assumeRole(AssumeRole.Request(
            ARN.of("arn:aws:iam::169766454405:role/ROLETEST"),
            UUID.randomUUID().toString()
        ))

        assertTrue(result.successValue()
            .AssumeRoleResponse.AssumeRoleResult
            .Credentials.Expiration.value.isAfter(ZonedDateTime.now(clock)))
    }
}
