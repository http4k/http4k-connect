package org.http4k.connect.amazon.sts

import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

abstract class STSContract(http: HttpHandler) : AwsContract(http) {

    protected val clock = Clock.systemDefaultZone()
    private val sts by lazy {
        STS.Http(aws.region, { aws.credentials }, http, clock)
    }

    @Test
    fun `assume role`() {
        val result = sts.assumeRole(
            ARN.of("arn:aws:iam::169766454405:role/ROLETEST"),
            UUID.randomUUID().toString(),
            durationSeconds = Duration.ofHours(1)
        )

        assertTrue(
            result.successValue()
                .Credentials.Expiration.value.isAfter(ZonedDateTime.now(clock))
        )
    }
}
