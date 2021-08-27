package org.http4k.connect.amazon.sts

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentials
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.sts.action.AssumedRole
import org.http4k.connect.amazon.sts.action.STSAction
import org.http4k.connect.amazon.sts.model.Credentials
import java.time.Clock
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.concurrent.atomic.AtomicReference

/**
 * Refreshing credentials provider for getting credentials based on assuming a role in STS.
 */
fun CredentialsProvider.Companion.STS(
    sts: STS,
    clock: Clock,
    gracePeriod: Duration = ofSeconds(300),
    assumeRole: () -> STSAction<out AssumedRole>
) = object : CredentialsProvider {
    private val credentials = AtomicReference<Credentials>(null)

    override fun invoke() = (credentials.get()?.takeIf { !it.expiresWithin(gracePeriod) } ?: refresh()).toHttp4k()

    private fun refresh() =
        synchronized(credentials) {
            val current = credentials.get()
            when {
                current != null && !current.expiresWithin(gracePeriod) -> current
                else -> when (val refresh = sts(assumeRole())) {
                    is Success<AssumedRole> -> {
                        val newCreds = refresh.value.Credentials
                        credentials.set(newCreds)
                        newCreds
                    }
                    is Failure<RemoteFailure> -> refresh.reason.throwIt()
                }
            }
        }

    private fun Credentials.expiresWithin(duration: Duration): Boolean =
        Expiration.value.toInstant()
            .minus(duration)
            .isBefore(clock.instant())
}

internal fun Credentials.toHttp4k() = AwsCredentials(AccessKeyId.value, SecretAccessKey.value, SessionToken.value)
