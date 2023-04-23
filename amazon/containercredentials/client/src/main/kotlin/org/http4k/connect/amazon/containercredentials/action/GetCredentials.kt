package org.http4k.connect.amazon.containercredentials.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.containercredentials.ContainerCredentials
import org.http4k.connect.amazon.containercredentials.ContainerCredentialsMoshi
import org.http4k.connect.amazon.core.model.Credentials
import org.http4k.connect.toRemoteFailure
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

@Http4kConnectAction
data class GetCredentials(private val uri: Uri) : ContainerCredentialsAction<Credentials> {
    override fun toRequest() = Request(GET, uri)

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(ContainerCredentialsMoshi.asA<Credentials>(bodyString()))
            else -> Failure(toRemoteFailure(this))
        }
    }
}

fun ContainerCredentials.getCredentials(uri: Uri) = this(GetCredentials(uri))
