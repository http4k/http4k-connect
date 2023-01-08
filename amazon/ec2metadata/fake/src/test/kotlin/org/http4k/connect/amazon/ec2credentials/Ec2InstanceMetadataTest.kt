package org.http4k.connect.amazon.ec2credentials

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.failureOrNull
import org.http4k.connect.amazon.core.model.Ec2ProfileName
import org.http4k.connect.amazon.ec2credentials.action.getAmiId
import org.http4k.connect.amazon.ec2credentials.action.getCredentials
import org.http4k.connect.amazon.ec2credentials.action.getHostName
import org.http4k.connect.amazon.ec2credentials.action.getIdentityDocument
import org.http4k.connect.amazon.ec2credentials.action.getInstanceId
import org.http4k.connect.amazon.ec2credentials.action.getInstanceType
import org.http4k.connect.amazon.ec2credentials.action.getLocalHostName
import org.http4k.connect.amazon.ec2credentials.action.getLocalIpv4
import org.http4k.connect.amazon.ec2credentials.action.getPublicHostName
import org.http4k.connect.amazon.ec2credentials.action.getPublicIpv4
import org.http4k.connect.amazon.ec2credentials.action.listProfiles
import org.http4k.connect.successValue
import org.http4k.core.Status.Companion.NOT_FOUND
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class Ec2InstanceMetadataTest {

    private var now = Instant.parse("2022-03-04T12:00:00Z")
    private val clock = object: Clock() {
        override fun instant() = now
        override fun withZone(zone: ZoneId?) = throw UnsupportedOperationException()
        override fun getZone() = ZoneOffset.UTC
    }

    private val metadata = InstanceMetadata(clock.instant())
    private val service = FakeEc2InstanceMetadata(clock, metadata)
    private val client = service.client()

    private val profile = Ec2ProfileName.of("default")

    @Test
    fun `list profiles`() = assertThat(
        client.listProfiles().successValue(),
        equalTo(listOf(profile))
    )

    @Test
    fun `get credentials`() {
        val credentials = client.getCredentials(profile).successValue()

        assertThat(
            credentials.LastUpdated,
            equalTo(ZonedDateTime.now(clock))
        )
        assertThat(
            credentials.Expiration.value.toInstant(),
            equalTo(now + Duration.ofHours(1))
        )

    }

    @Test
    fun `get credentials - reuse if not expired`() = assertThat(
        client.getCredentials(profile).successValue().also { println(it) },
        equalTo(client.getCredentials(profile).successValue().also { println(it) })
    )

    @Test
    fun `get credentials - renew if expired`() {
        val oldCredentials = client.getCredentials(profile).successValue()
        now += Duration.ofHours(2)

        val newCredentials = client.getCredentials(profile).successValue()

        assertThat(
            newCredentials,
            equalTo(oldCredentials).not()
        )
        assertThat(
            newCredentials.Expiration.value.toInstant(),
            equalTo(now + Duration.ofHours(1))
        )
    }

    @Test
    fun `get credentials for missing profile`() = assertThat(
        client.getCredentials(Ec2ProfileName.of("missing")).failureOrNull()?.status,
        equalTo(NOT_FOUND)
    )

    @Test
    fun `get ami id`() = assertThat(
        client.getAmiId().successValue(),
        equalTo(metadata.imageId)
    )

    @Test
    fun `get identity document`() = assertThat(
        client.getIdentityDocument().successValue(),
        equalTo(metadata.identity)
    )

    @Test
    fun `get public hostname`() = assertThat(
        client.getPublicHostName().successValue(),
        equalTo(metadata.publicHostName)
    )

    @Test
    fun `get hostname`() = assertThat(
        client.getHostName().successValue(),
        equalTo(metadata.privateHostName)
    )

    @Test
    fun `get local hostname`() = assertThat(
        client.getLocalHostName().successValue(),
        equalTo(metadata.privateHostName)
    )

    @Test
    fun `get public ipv4`() = assertThat(
        client.getPublicIpv4().successValue(),
        equalTo(metadata.publicIp)
    )

    @Test
    fun `get local ipv4`() = assertThat(
        client.getLocalIpv4().successValue(),
        equalTo(metadata.privateIp)
    )

    @Test
    fun `get instance id`() = assertThat(
        client.getInstanceId().successValue(),
        equalTo(metadata.instanceId)
    )

    @Test
    fun `get instance type`() = assertThat(
        client.getInstanceType().successValue(),
        equalTo(metadata.instanceType)
    )
}
