package org.http4k.connect.amazon.core.model

import org.http4k.aws.AwsCredentials
import se.ansman.kotshi.JsonSerializable
import java.time.Clock
import java.time.Duration
import java.time.ZonedDateTime

@JsonSerializable
data class Ec2Credentials(
    val Code: String,
    val LastUpdated: ZonedDateTime,
    val Type: String,
    val AccessKeyId: AccessKeyId,
    val SecretAccessKey: SecretAccessKey,
    val Token: SessionToken,
    val Expiration: Expiration
) {
    fun expiresWithin(clock: Clock, duration: Duration): Boolean =
        Expiration.value.toInstant()
            .minus(duration)
            .isBefore(clock.instant())

    fun asHttp4k() = AwsCredentials(AccessKeyId.value, SecretAccessKey.value, Token.value)
}
