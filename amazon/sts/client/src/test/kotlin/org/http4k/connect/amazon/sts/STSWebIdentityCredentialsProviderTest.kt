package org.http4k.connect.amazon.sts

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.http4k.cloudnative.env.Environment.Companion.EMPTY
import org.http4k.connect.amazon.AWS_ROLE_ARN
import org.http4k.connect.amazon.AWS_WEB_IDENTITY_TOKEN_FILE
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.connect.amazon.core.model.SessionToken
import org.http4k.connect.amazon.sts.action.AssumeRole
import org.http4k.connect.amazon.sts.action.AssumeRoleWithWebIdentity
import org.http4k.connect.amazon.sts.action.AssumedRoleWithWebIdentityResponse
import org.http4k.connect.amazon.sts.model.AssumedRoleUser
import org.http4k.connect.amazon.sts.model.Credentials
import org.http4k.connect.amazon.sts.model.Expiration
import org.http4k.connect.amazon.sts.model.RoleId
import org.http4k.core.with
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class STSWebIdentityCredentialsProviderTest {

    private val sts = mockk<STS>()
    private val now = Instant.now()
    private val clock = TestClock(now)

    private val env = EMPTY
        .with(
            AWS_ROLE_ARN of ARN.of("arn:aws:sts:us-east-1:000000000001:role:myrole"),
            AWS_WEB_IDENTITY_TOKEN_FILE of File(javaClass.getResource("/webidentitytoken.txt").file)
        )

    private val provider = CredentialsProvider.STSWebIdentity(env, sts, clock, Duration.ofSeconds(60))

    @Test
    fun `gets credentials first time only`() {
        val firstCreds = credentialsExpiringAt(now.plusSeconds(61), 1)
        every { sts.invoke(any<AssumeRoleWithWebIdentity>()) } returns assumedRole(firstCreds)

        assertThat(provider(), equalTo(firstCreds.toHttp4k()))
        clock.tickBy(Duration.ofSeconds(1))
        assertThat(provider(), equalTo(firstCreds.toHttp4k()))

        verify(exactly = 1) { sts.invoke(any<AssumeRole>()) }
    }

    private fun assumedRole(credentials: Credentials) = Success(
        AssumedRoleWithWebIdentityResponse(
            AssumedRoleUser(arn, RoleId.of("hello")),
            credentials,
            "subject",
            "audience",
            "source",
            "provider"
        )
    )

    private fun credentialsExpiringAt(expiry: Instant, counter: Int) = Credentials(
        SessionToken.of("SessionToken"),
        AccessKeyId.of(counter.toString()),
        SecretAccessKey.of("SecretAccessKey"),
        Expiration.of(ZonedDateTime.ofInstant(expiry, ZoneId.of("UTC")))
    )

    private val arn = ARN.of("arn:aws:foobar")
}
