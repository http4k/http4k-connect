package org.http4k.connect.amazon.containercredentials

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AWS_CONTAINER_CREDENTIALS_RELATIVE_URI
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.containercredentials.action.getCredentials
import org.http4k.connect.amazon.core.model.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import java.time.Clock
import java.time.Duration
import java.util.concurrent.atomic.AtomicReference

/**
 * Refreshing credentials provider for getting credentials from the container credentials service .
 */
fun CredentialsProvider.Companion.ContainerCredentials(
    containerCredentials: ContainerCredentials,
    relativePathUri: Uri,
    clock: Clock,
    gracePeriod: Duration
) = object : CredentialsProvider {

    private val credentials = AtomicReference<Credentials>(null)

    override fun invoke() =
        (credentials.get()?.takeIf { !it.expiresWithin(clock, gracePeriod) } ?: refresh()).asHttp4k()

    private fun refresh() =
        synchronized(credentials) {
            val current = credentials.get()
            when {
                current != null && !current.expiresWithin(clock, gracePeriod) -> current
                else -> when (val refreshed = containerCredentials.getCredentials(relativePathUri)) {
                    is Success<Credentials> -> {
                        val newCreds = refreshed.value
                        credentials.set(newCreds)
                        newCreds
                    }
                    is Failure<RemoteFailure> -> refreshed.reason.throwIt()
                }
            }
        }
}

fun CredentialsProvider.Companion.ContainerCredentials(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    gracePeriod: Duration = Duration.ofSeconds(300)
) = CredentialsProvider.ContainerCredentials(
    ContainerCredentials.Http(http),
    AWS_CONTAINER_CREDENTIALS_RELATIVE_URI(env),
    clock, gracePeriod
)
