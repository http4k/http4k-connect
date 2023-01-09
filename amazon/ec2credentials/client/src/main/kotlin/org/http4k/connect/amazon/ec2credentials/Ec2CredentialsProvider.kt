package org.http4k.connect.amazon.ec2credentials

import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.valueOrNull
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.CredentialsChain
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.ec2credentials.action.getCredentials
import org.http4k.connect.amazon.ec2credentials.action.listProfiles
import org.http4k.core.HttpHandler
import java.time.Clock
import java.time.Duration
import java.util.concurrent.atomic.AtomicReference

@Deprecated("Use http4k-connect-amazon-ec2metadata module")
fun CredentialsChain.Companion.Ec2InstanceProfile(
    ec2InstanceMetadata: Ec2InstanceMetadata,
    clock: Clock,
    gracePeriod: Duration
): CredentialsChain {
    val cached = AtomicReference<Ec2Credentials>(null)

    fun refresh() = synchronized(cached) {
        val current = cached.get()
        if (current != null && !current.expiresWithin(clock, gracePeriod)) {
            current
        } else {
            ec2InstanceMetadata
                .listProfiles()
                .onFailure { it.reason.throwIt() }
                .asSequence()
                .map { ec2InstanceMetadata.getCredentials(it) }
                .mapNotNull { it.valueOrNull() }
                .firstOrNull()
                ?.also { cached.set(it) }
        }
    }

    return CredentialsChain {
        val credentials = cached.get()
            ?.takeIf { !it.expiresWithin(clock, gracePeriod) }
            ?: refresh()
        credentials?.asHttp4k()
    }
}

@Deprecated("Use http4k-connect-amazon-ec2metadata module")
fun CredentialsChain.Companion.Ec2InstanceProfile(
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    gracePeriod: Duration = Duration.ofSeconds(30)
) = CredentialsChain.Ec2InstanceProfile(
    ec2InstanceMetadata = Ec2InstanceMetadata.Http(http),
    clock = clock,
    gracePeriod = gracePeriod
)

@Deprecated("Use http4k-connect-amazon-ec2metadata module")
fun CredentialsProvider.Companion.Ec2InstanceProfile(
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    gracePeriod: Duration = Duration.ofSeconds(30)
) = CredentialsChain.Ec2InstanceProfile(
    ec2InstanceMetadata = Ec2InstanceMetadata.Http(http),
    clock = clock,
    gracePeriod = gracePeriod
).provider()

