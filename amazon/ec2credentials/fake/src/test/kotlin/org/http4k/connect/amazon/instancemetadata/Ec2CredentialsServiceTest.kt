package org.http4k.connect.amazon.instancemetadata

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.failureOrNull
import org.http4k.connect.amazon.core.model.Ec2ProfileName
import org.http4k.connect.amazon.instancemetadata.action.getCredentials
import org.http4k.connect.amazon.instancemetadata.action.listProfiles
import org.http4k.connect.successValue
import org.http4k.core.Status.Companion.NOT_FOUND
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class Ec2CredentialsServiceTest {

    private val clock = Clock.fixed(Instant.parse("2022-03-04T12:00:00Z"), ZoneOffset.UTC)
    private val defaultSessionValidity = Duration.ofHours(1)
    private val service = FakeEc2CredentialService(clock = clock, defaultSessionValidity = defaultSessionValidity)
    private val client = service.client()

    @Test
    fun `list profiles`() {
        assertThat(
            client.listProfiles().successValue(),
            equalTo(listOf(Ec2ProfileName.of("default")))
        )
    }

    @Test
    fun `credentials not expired`() {
        val credentials = client.getCredentials(Ec2ProfileName.of("default")).successValue()

        assertThat(
            credentials.LastUpdated,
            equalTo(ZonedDateTime.now(clock))
        )
        assertThat(
            Duration.between(clock.instant(), credentials.Expiration.value),
            equalTo(defaultSessionValidity)
        )
    }

    @Test
    fun `get credentials for missing profile`() {
        val result = client.getCredentials(Ec2ProfileName.of("missing"))
        assertThat(
            result.failureOrNull()?.status,
            equalTo(NOT_FOUND)
        )
    }
}
