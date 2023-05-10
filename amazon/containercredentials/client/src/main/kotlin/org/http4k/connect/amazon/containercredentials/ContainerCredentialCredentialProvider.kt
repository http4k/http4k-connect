package org.http4k.connect.amazon.containercredentials

import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.peek
import dev.forkhandles.result4k.valueOrNull
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.CredentialsChain
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.containercredentials.action.getCredentials
import org.http4k.connect.amazon.core.model.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import java.time.Clock
import java.time.Duration
import java.util.concurrent.atomic.AtomicReference

private fun credentialProvider(
    containerCredentials: ContainerCredentials,
    uri: Uri,
    clock: Clock,
    gracePeriod: Duration
): () -> Result4k<Credentials, RemoteFailure> {
    val credentials = AtomicReference<Credentials>(null)

    fun refresh(): Result4k<Credentials, RemoteFailure> =
        synchronized(credentials) {
            val current = credentials.get()
            when {
                current != null && !current.expiresWithin(clock, gracePeriod) -> Success(current)
                else -> containerCredentials
                    .getCredentials(uri)
                    .peek { credentials.set(it) }
            }
        }

    return {
        credentials.get()
            ?.takeUnless { it.expiresWithin(clock, gracePeriod) }
            ?.let { Success(it) }
            ?: refresh()
    }
}

/**
 * Refreshing credentials provider for getting credentials from the container credentials service .
 */
fun CredentialsChain.Companion.ContainerCredentials(
    containerCredentials: ContainerCredentials,
    uri: Uri,
    clock: Clock,
    gracePeriod: Duration
): CredentialsChain {
    val provider = credentialProvider(containerCredentials, uri, clock, gracePeriod)

    return CredentialsChain {
        provider().map { it.asHttp4k() }.valueOrNull()
    }
}

fun CredentialsChain.Companion.ContainerCredentials(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    gracePeriod: Duration = Duration.ofSeconds(300)
) = CredentialsChain.ContainerCredentials(
    ContainerCredentials.Http(http, AWS_CONTAINER_AUTHORIZATION_TOKEN(env)),
    AWS_CONTAINER_CREDENTIALS_FULL_URI(env),
    clock, gracePeriod
)

/**
 * Refreshing credentials provider for getting credentials from the container credentials service .
 */
fun CredentialsProvider.Companion.ContainerCredentials(
    containerCredentials: ContainerCredentials,
    uri: Uri,
    clock: Clock,
    gracePeriod: Duration
): CredentialsProvider {
    val provider = credentialProvider(containerCredentials, uri, clock, gracePeriod)

    return CredentialsProvider {
        provider().map { it.asHttp4k() }.onFailure { it.reason.throwIt() }
    }
}

fun CredentialsProvider.Companion.ContainerCredentials(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    gracePeriod: Duration = Duration.ofSeconds(300)
) = CredentialsProvider.ContainerCredentials(
    ContainerCredentials.Http(http, AWS_CONTAINER_AUTHORIZATION_TOKEN(env)),
    AWS_CONTAINER_CREDENTIALS_FULL_URI(env),
    clock, gracePeriod
)
