package org.http4k.connect.amazon.sts

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AccessKeyId
import org.http4k.connect.amazon.model.AssumedRoleUser
import org.http4k.connect.amazon.model.Credentials
import org.http4k.connect.amazon.model.Expiration
import org.http4k.connect.amazon.model.RoleId
import org.http4k.connect.amazon.model.SecretAccessKey
import org.http4k.connect.amazon.model.SessionToken
import org.http4k.connect.amazon.sts.action.AssumeRole
import org.http4k.connect.amazon.sts.action.AssumedRole
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class STSCredentialsProviderTest {

    private val sts = mockk<STS>()

    @Test
    fun `gets credentials first time only`() {
        val now = Instant.now()
        val firstCreds = credentialsExpiringAt(now.plusSeconds(100), 1)

        every { sts.invoke(any<AssumeRole>()) } returns assumedRole(firstCreds)

        val provider = STSCredentialsProvider(sts, Clock.fixed(now, ZoneId.of("UTC")), requestProvider, Duration.ofSeconds(60))

        assertThat(provider(), equalTo(firstCreds.toHttp4k()))
        assertThat(provider(), equalTo(firstCreds.toHttp4k()))

        verify(exactly = 1) { sts.invoke(any<AssumeRole>()) }
    }

    private fun assumedRole(credentials: Credentials) = Success(
        AssumedRole(AssumedRoleUser(arn, RoleId.of("hello")),
            credentials))

    private fun credentialsExpiringAt(expiry: Instant, counter: Int) = Credentials(SessionToken.of("SessionToken"),
        AccessKeyId.of(counter.toString()),
        SecretAccessKey.of("SecretAccessKey"),
        Expiration.of(ZonedDateTime.ofInstant(expiry, ZoneId.of("UTC")))
    )

    private val requestProvider: () -> AssumeRole = { AssumeRole(arn, "Session") }

    private val arn = ARN.of("arn:aws:foobar")
}
